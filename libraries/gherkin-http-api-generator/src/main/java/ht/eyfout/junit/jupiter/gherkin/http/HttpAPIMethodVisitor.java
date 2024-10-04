package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Optional;
import java.util.function.Function;

class HttpAPIMethodVisitor extends HttpMethodVisitor {
    private final SwaggerAPI swagger;
    private final String methodName;
    private final Function<String, String> rename;
    private final String optionalClass = Type.getType(Optional.class).getInternalName();

    public HttpAPIMethodVisitor(int api,
                                MethodVisitor sink,
                                SwaggerAPI swagger,
                                String name,
                                Function<String, String> rename) {
        super(api, sink, rename);
        this.swagger = swagger;
        this.methodName = name;
        this.rename = rename;

    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, rename.apply(type));
    }

    @Override
    public void visitLdcInsn(Object value) {
        if (methodName.equals(HttpAPIMethodVisitor.GjCGMethod.GET_HTTP_METHOD.method())) {
            super.visitLdcInsn(swagger.httpMethod());
        } else if (methodName.equals(HttpAPIMethodVisitor.GjCGMethod.GET_BASE_PATH.method())) {
            super.visitLdcInsn(swagger.path());
        } else {
            super.visitLdcInsn(value);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (methodName.equals(HttpAPIMethodVisitor.GjCGMethod.GET_DESCRIPTION.method()) && owner.equals(optionalClass)) {
            super.visitLdcInsn(swagger.description());
            super.visitMethodInsn(opcode, owner, "of", Type.getMethodDescriptor(Type.getType(Optional.class), Type.getType(Object.class)), isInterface);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    enum GjCGMethod {
        GET_HTTP_METHOD("getHttpMethod"),
        GET_BASE_PATH("getBasePath"),
        GET_DESCRIPTION("getDescription");
        private final String methodName;

        GjCGMethod(String methodName) {
            this.methodName = methodName;
        }

        String method() {
            try {
                return GjCGHttpAPI.class.getMethod(methodName).getName();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(methodName, e);
            }
        }
    }
}
