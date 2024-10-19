package ht.eyfout.openapi.http.api;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.function.Function;

class HttpMethodVisitor extends MethodVisitor {

    private final Function<String, String> rename;

    public HttpMethodVisitor(int api,
                             MethodVisitor sink,
                             Function<String, String> rename
    ) {
        super(api, sink);
        this.rename = rename;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, rename(type));
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(rename(name), rename(descriptor), rename(signature), start, end, index);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, rename(owner), rename(name), rename(descriptor), isInterface);
    }


    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(rename(name), access);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, rename(owner), name, rename(descriptor));
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    String rename(String it) {
        return this.rename.apply(it);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        Object[] args = Arrays.stream(bootstrapMethodArguments).map(it -> {
            if (it instanceof Handle) {
                return handle((Handle) it);
            } else if (it instanceof Type) {
                return type((Type) it);
            } else {
                return it;
            }
        }).toArray(Object[]::new);
        super.visitInvokeDynamicInsn(name, rename(descriptor), handle(bootstrapMethodHandle), args);
    }

    private Handle handle(Handle other) {
        return new Handle(other.getTag(), rename(other.getOwner()), other.getName(), rename(other.getDesc()));
    }

    private Type type(Type other) {
        return Type.getType(rename(other.getInternalName()));
    }
}
