package ht.eyfout.openapi.http.api;

import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.Objects;

public class HttpRequiredParamMethodVisitor extends MethodVisitor {
    private final String owner;
    private final String builderClass;
    /**
     * (Type, Field Name)
     */
    private final Collection<Parameter> params;


    public HttpRequiredParamMethodVisitor(int api, MethodVisitor methodVisitor,
                                          String owner,
                                          String builderClass,
                                          Collection<Parameter> params) {
        super(api, methodVisitor);
        this.owner = owner;
        this.builderClass = builderClass;
        this.params = params;
    }

    /**
     * Objects.requireNonNull(httpBuilder.header("orderID"), "orderID")
     */
    @Override
    public void visitCode() {
        super.visitCode();
        if (!params.isEmpty()) {
            super.visitParameter("httpBuilder", Opcodes.ACC_SYNTHETIC);
            params.forEach(it -> {
                super.visitVarInsn(Opcodes.ALOAD, 1);
                super.visitLdcInsn(it.getName());
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        builderClass,
                        HttpParamClassVisitor.aliasForIn.get(it.getIn()),
                        Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(String.class)),
                        false);
                super.visitLdcInsn(it.getName());
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        Type.getType(Objects.class).getInternalName(),
                        "requireNonNull",
                        Type.getMethodDescriptor(Type.getType(void.class), Type.getType(Object.class), Type.getType(String.class)),
                        false);
            });
        }
    }
}
