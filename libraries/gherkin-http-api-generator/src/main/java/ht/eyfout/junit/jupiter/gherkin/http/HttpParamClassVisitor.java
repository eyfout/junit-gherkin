package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
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
    private String className;
    /**
     * (Type of builder, Field name)
     */
    private Pair<String, String> builder;

    HttpParamClassVisitor(int api, ClassVisitor cv, Collection<Parameter> params, Function<String, String> rename) {
        super(api, cv);
        this.params = params;
        builderClass = rename.apply(Type.getType(HttpAPIRequestBuilder.class).getInternalName());
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (descriptor.contains(builderClass)) {
            builder = new Pair<>(descriptor, name);
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
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(HttpParamMethod.REQUIRED_PARAMETER.method())) {
            return new HttpRequiredParamMethodVisitor(api,
                    mv,
                    className,
                    builderClass,
                    params.stream().filter(Parameter::getRequired).toList());
        }
        return mv;
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
        String owner = Type.getType(HttpAPIRequestBuilder.class).getInternalName();
        String descriptor = Type.getMethodDescriptor(Type.getType(void.class), Type.getType(String.class), Type.getType(Object.class));

        params.forEach(it -> {
            MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
                    "set" + GherkinHttpAPIGenerator.camelCase(it.getName()),
                    Type.getMethodDescriptor(Type.getType(void.class), Type.getType(type(it.getSchema()))),
                    null,
                    null);
            String name = it.getName().replace('-', '_');
            mv.visitCode();
            mv.visitParameter(name, Opcodes.ACC_SYNTHETIC);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, className, builder.second(), builder.first());
            mv.visitLdcInsn(name);
            mv.visitVarInsn(Opcodes.ILOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner,
                    aliasForIn.get(it.getIn().toLowerCase()),
                    descriptor, false);
            mv.visitEnd();
        });
        super.visitEnd();
    }

    enum HttpParamMethod {
        REQUIRED_PARAMETER("requiredParams", HttpAPIRequestBuilder.class);
        private final String methodName;
        private final Class<?> types;

        HttpParamMethod(String methodName, Class<?> types) {
            this.methodName = methodName;
            this.types = types;
        }

        String method() {
            try {
                return GjCGRequestBuilder.Param.class.getMethod(methodName, types).getName();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(methodName, e);
            }
        }
    }
}
