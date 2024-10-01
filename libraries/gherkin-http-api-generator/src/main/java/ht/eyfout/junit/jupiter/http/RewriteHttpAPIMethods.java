package ht.eyfout.junit.jupiter.http;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

class RewriteHttpAPIMethods extends MethodVisitor {

    List<MethodNode> methodNodes = new ArrayList<>();

    public RewriteHttpAPIMethods(int api) {
        super(api);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
    }
}
