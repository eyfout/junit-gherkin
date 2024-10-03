package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class GjCGCodeGenerator {
    private static ClassReader asClassReader(Class<?> klass) {
        try {
            return new ClassReader(klass.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String camelCase(String it) {
        return it.toUpperCase().charAt(0) + it.substring(1);
    }

    static String replace(String it, String other) {
        if (it != null) {
            return it.replace("GjCG", other);
        }
        return it;
    }

    static public void generate(URL in) {
        Set<Class<?>> paramType = Arrays.stream(GjCGRequestBuilder.class.getDeclaredClasses()).collect(Collectors.toSet());
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        paths.entrySet().stream().parallel().flatMap(path ->
                path.getValue().readOperationsMap().entrySet().stream().map(it -> new SwaggerAPI(path.getKey(), it.getKey(), it.getValue()))
        ).forEach(api ->
                Stream.of(GjCGHttpAPI.class, GjCGRequestBuilder.class)
                        .flatMap(it -> Stream.concat(Stream.of(it), Arrays.stream(it.getDeclaredClasses())))
                        .forEach(klass -> {
                            int dot = klass.getName().lastIndexOf('.');
                            String fName = klass.getName().substring(dot + 1).replace("GjCG", api.id()) + ".class";
                            byte[] content = generate(klass, api, it -> replace(it, api.id()));
                            if (paramType.contains(klass)) {
                                content = with(klass, content, api);
                            }
                            createFile(rootDir, fName, klass, content);
                        })
        );
    }

    private static byte[] with(Class<?> klass, byte[] content, SwaggerAPI api) {
        ClassReader reader = new ClassReader(content);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        Collection<Parameter> parameterPairs = new ArrayList<>();
        Optional.ofNullable(api.operation().getParameters()).ifPresent(params -> {
            List<Parameter> pairs = params.stream()
                    .filter(it -> it.getIn() != null && it.getName() != null)
                    .filter(it -> klass.getName().contains(camelCase(it.getIn())))
                    .toList();
            parameterPairs.addAll(pairs);
        });
        reader.accept(new ParamClassVisitor(Opcodes.ASM7, writer, parameterPairs), ClassWriter.COMPUTE_FRAMES);
        return writer.toByteArray();
    }

    private static void createFile(File rootDir, String fName, Class<?> klass, byte[] content) {
        int dot = klass.getName().lastIndexOf(".");
        File pkg = new File(rootDir, klass.getName().substring(0, dot).replace('.', '/') + "/");
        pkg.mkdirs();
        File file = new File(pkg, fName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generate(Class<?> klass, SwaggerAPI api, Function<String, String> rename) {
        return generate(klass, (source, sink) -> new HttpClassVisitor(Opcodes.ASM7, source, sink, rename,
                        (name, descriptor, it) -> {
                            if (klass == GjCGHttpAPI.class) {
                                return new HttpMethodVisitor.APIMethodVisitor(Opcodes.ASM7, null, it, api, name, descriptor);
                            } else {
                                return new HttpMethodVisitor(Opcodes.ASM7, null, it);
                            }
                        })
                , rename);

    }

    private static byte[] generate(Class<?> klass,
                                   BiFunction<ClassVisitor, ClassVisitor, ClassVisitor> visitor,
                                   Function<String, String> rename) {
        ClassReader reader = asClassReader(klass);
        ClassWriter source = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassWriter sink = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        reader.accept(visitor.apply(source, sink), 0);
        return sink.toByteArray();
    }

    private static class ParamClassVisitor extends ClassVisitor {
        private static final Map<String, String> api = Map.of(
                "path", "pathParam",
                "header", "header",
                "query", "queryParam"
        );
        private final Collection<Parameter> params;
        private String vistingClassName;
        private String builderType;

        ParamClassVisitor(int api, ClassVisitor cv, Collection<Parameter> params) {
            super(api, cv);
            this.params = params;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            vistingClassName = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (name.contains("builder")) {
                builderType = descriptor;
            }
            return super.visitField(access, name, descriptor, signature, value);
        }

        private Class<?> type(String type) {
            String ltype = Objects.requireNonNullElse(type, "");
            type.toLowerCase();
            if (ltype.contains("int")) {
                return int.class;
            } else if (ltype.contains("num")) {
                return double.class;
            } else {
                return String.class;
            }
        }

        private Class<?> type(Schema<?> schema) {
            if (schema == null) {
                return type("");
            } else {
                return type(schema.getType());
            }
        }

        @Override
        public void visitEnd() {
            String owner = Type.getType(HttpAPIRequestBuilder.class).getInternalName();
            String descriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(String.class));

            params.forEach(it -> {
                MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                        "set" + camelCase(it.getName()),
                        Type.getMethodDescriptor(Type.getType(void.class), Type.getType(type(it.getSchema()))),
                        null,
                        null);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, vistingClassName, "builder", builderType);
                mv.visitLdcInsn(it.getName());
                mv.visitVarInsn(Opcodes.ILOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner,
                        api.get(it.getIn().toLowerCase()),
                        descriptor, false);
                mv.visitEnd();
            });
            super.visitEnd();
        }
    }

    private static class HttpClassVisitor extends ClassVisitor {

        private final Function<String, String> rebrand;
        private final TriFunction<String, String, MethodVisitor, MethodVisitor> methodVisitor;

        public HttpClassVisitor(int api,
                                ClassVisitor source,
                                ClassVisitor sink,
                                Function<String, String> rebrand,
                                TriFunction<String, String, MethodVisitor, MethodVisitor> methodVisitor) {
            super(api, sink);
            this.rebrand = rebrand;
            this.methodVisitor = methodVisitor;
        }

        @Override
        public void visitAttribute(Attribute attribute) {
            invoke(method("visitAttribute", Attribute.class), attribute);
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
            return invoke(method("visitTypeAnnotation", int.class, TypePath.class, String.class, boolean.class),
                    typeRef, typePath, descriptor, visible);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            return invoke(method("visitAnnotation", String.class, boolean.class), descriptor, visible);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            invoke(method("visit", int.class, int.class, String.class, String.class, String.class, String[].class), version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return methodVisitor.apply(rename(name), rename(descriptor), cv.visitMethod(access, rename(name), rename(descriptor), rename(signature), exceptions));
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            invoke(method("visitInnerClass", String.class, String.class, String.class, int.class), name, outerName, innerName, access);
        }

        @Override
        public void visitNestMember(String nestMember) {
            invoke(method("visitNestMember", String.class), nestMember);
        }

        @Override
        public void visitOuterClass(String owner, String name, String descriptor) {
            invoke(method("visitOuterClass", String.class, String.class, String.class), owner, name, descriptor);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            return invoke(method("visitField", int.class, String.class, String.class, String.class, Object.class), access, name, descriptor, signature, value);
        }

        @Override
        public void visitEnd() {
            invoke(method("visitEnd"));
        }

        @Override
        public void visitSource(String source, String debug) {
            invoke(method("visitSource", String.class, String.class), source, debug);
        }

        @Override
        public ModuleVisitor visitModule(String name, int access, String version) {
            return invoke(method("visitModule", String.class, int.class, String.class), name, access, version);
        }

        @Override
        public void visitPermittedSubclass(String permittedSubclass) {
            invoke(method("visitPermittedSubclass"));
        }

        private Method method(String name, Class<?>... types) {
            try {
                return ClassVisitor.class.getMethod(name, types);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        private <R> R invoke(Method method, Object... arguments) {
            try {
                return (R) method.invoke(cv, Arrays.stream(arguments).map(this::rename).toArray());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Object rename(Object it) {
            if (it instanceof String) {
                return rebrand.apply((String) it);
            }
            return it;
        }

        private String rename(String it) {
            return rebrand.apply(it);
        }
    }

    private static class HttpMethodVisitor extends MethodVisitor {

        private final MethodVisitor source;

        public HttpMethodVisitor(int api,
                                 MethodVisitor source,
                                 MethodVisitor sink
        ) {
            super(api, sink);
            this.source = source;
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitLabel(Label label) {
            super.visitLabel(label);
        }

        @Override
        public void visitParameter(String name, int access) {
            super.visitParameter(name, access);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitEnd() {
            Optional.ofNullable(source).ifPresent(MethodVisitor::visitEnd);
            super.visitEnd();
        }

        @Override
        public void visitCode() {
            Optional.ofNullable(source).ifPresent(MethodVisitor::visitCode);
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            Optional.ofNullable(source).ifPresent(it -> it.visitInsn(opcode));
            super.visitInsn(opcode);
        }

        static private class APIMethodVisitor extends HttpMethodVisitor {
            private final SwaggerAPI swagger;
            private final String name;
            private final String descriptor;

            public APIMethodVisitor(int api, MethodVisitor source,
                                    MethodVisitor sink,
                                    SwaggerAPI swagger,
                                    String name,
                                    String descriptor) {
                super(api, source, sink);
                this.swagger = swagger;
                this.name = name;
                this.descriptor = descriptor;
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                super.visitTypeInsn(opcode, type.replace("GjCG", swagger.id()));
            }

            @Override
            public void visitLdcInsn(Object value) {
                if (name.equals("getDescription")) {
                    super.visitLdcInsn(swagger.description());
                } else if (name.equals("getHttpMethod")) {
                    super.visitLdcInsn(swagger.httpMethod());
                } else if (name.equals("getBasePath")) {
                    super.visitLdcInsn(swagger.path());
                } else {
                    super.visitLdcInsn(value);
                }
            }
        }
    }
}
