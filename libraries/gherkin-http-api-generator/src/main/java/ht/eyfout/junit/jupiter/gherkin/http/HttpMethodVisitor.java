package ht.eyfout.junit.jupiter.gherkin.http;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.Optional;
import java.util.function.Function;

class HttpMethodVisitor extends MethodVisitor {

    private final MethodVisitor source;
    private final Function<String, String> rename;

    public HttpMethodVisitor(int api,
                             MethodVisitor source,
                             MethodVisitor sink,
                             Function<String, String> rename
    ) {
        super(api, sink);
        this.source = source;
        this.rename = rename;
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, rename(owner), name, rename(descriptor), isInterface);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, rename(type));
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

    String rename(String it) {
        return this.rename.apply(it);
    }
}
