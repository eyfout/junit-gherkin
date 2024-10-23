package ht.eyfout.openapi.http.generator;

import ht.eyfout.http.HttpRequestBuilder;
import ht.eyfout.http.openapi.generated.GjCGHttpEndpoint;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

class HttpParamClassVisitor extends ClassVisitor {
    static final Map<String, String> aliasForIn = Map.of(
            "path", "pathParam",
            "header", "header",
            "query", "queryParam"
    );
    private final Collection<Parameter> params;
    private final String builderClass;
    private final Function<String, String> rename;
    private String className;
    /**
     * (Type of builder, Field name)
     */
    private Pair<String, String> builder;

    HttpParamClassVisitor(int api, ClassVisitor cv, Collection<Parameter> params, Function<String, String> rename) {
        super(api, cv);
        this.params = params;
        builderClass = rename.apply(Type.getType(HttpRequestBuilder.class).getInternalName());
        this.rename = rename;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, rename(name), rename(signature), rename(superName), interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (descriptor.contains(builderClass)) {
            builder = new Pair<>(rename(descriptor), name);
        }
        return super.visitField(access, name, rename(descriptor), rename(signature), value);
    }

    private int load(Class<?> klass) {
        if (klass == int.class) {
            return Opcodes.ILOAD;
        } else if (klass == double.class) {
            return Opcodes.DLOAD;
        }
        return Opcodes.ALOAD;
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
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, rename(name), rename(descriptor), rename(signature), exceptions);
        if (name.equals(HttpParamMethod.REQUIRED_PARAMETER.method())) {
            return new HttpRequiredParamMethodVisitor(api,
                    mv,
                    className,
                    builderClass,
                    params.stream().filter(Parameter::getRequired).toList());
        }
        return new HttpMethodVisitor(api, mv, rename);
    }

    /**
     * Setter method query, path or header parameter, i.e:
     * <p>
     * void setAuthorization(String authorization){
     * this.builder.header("authorization", authorization);
     * }
     */
    @Override
    public void visitEnd() {
        Type owner = Type.getType(HttpRequestBuilder.class);
        Type str = Type.getType(String.class);
        String descriptor = Type.getMethodDescriptor(owner, str, Type.getType(Object.class));


        params.forEach(it -> {
            Class<?> paramType = type(it.getSchema());
            String paramName = param(it.getName());
            cv.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                    paramName.toUpperCase(), str.getDescriptor(), null, null);

            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                    "set" + OpenAPIHttpEndpointGenerator.camelCase(it.getName()),
                    Type.getMethodDescriptor(Type.getType(void.class), Type.getType(paramType)),
                    null,
                    null);
            mv.visitCode();

            mv.visitParameter(paramName, Opcodes.ACC_SYNTHETIC);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, className, builder.second(), builder.first());
            mv.visitLdcInsn(paramName);
            mv.visitVarInsn(load(paramType), 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner.getInternalName(),
                    aliasForIn.get(it.getIn().toLowerCase()),
                    descriptor, false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        });

        MethodVisitor clinit = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        clinit.visitCode();
        params.forEach(it ->{
            clinit.visitLdcInsn(it.getName());
            clinit.visitFieldInsn(Opcodes.PUTSTATIC, className, param(it.getName()).toUpperCase(), str.getDescriptor());
        });

        clinit.visitMaxs(0,0);
        clinit.visitInsn(Opcodes.RETURN);
        clinit.visitEnd();
        super.visitEnd();
    }

    String rename(String other) {
        return rename.apply(other);
    }

    String param(String name) {
        return name.replace('-', '_');
    }

    enum HttpParamMethod {
        REQUIRED_PARAMETER("requiredParams", HttpRequestBuilder.class);
        private final String methodName;
        private final Class<?> types;

        HttpParamMethod(String methodName, Class<?> types) {
            this.methodName = methodName;
            this.types = types;
        }

        String method() {
            try {
                return GjCGHttpEndpoint.Param.class.getMethod(methodName, types).getName();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(methodName, e);
            }
        }
    }
}
