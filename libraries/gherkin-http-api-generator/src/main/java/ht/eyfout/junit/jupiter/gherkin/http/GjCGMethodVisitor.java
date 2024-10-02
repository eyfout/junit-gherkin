package ht.eyfout.junit.jupiter.gherkin.http;

import org.objectweb.asm.MethodVisitor;

import java.util.Collection;

public class GjCGMethodVisitor extends MethodVisitor {

    private final Collection<MethodVisitor> targets;
    public GjCGMethodVisitor(int api, MethodVisitor source, Collection<MethodVisitor> targets) {
        super(api, source);
        this.targets = targets;

    }

    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
        targets.forEach(it -> {
            it.visitParameter(name, access);
        });

    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        targets.forEach(MethodVisitor::visitEnd);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        targets.forEach(MethodVisitor::visitCode);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);

    }
}
