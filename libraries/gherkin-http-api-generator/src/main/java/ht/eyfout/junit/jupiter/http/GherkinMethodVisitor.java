package ht.eyfout.junit.jupiter.http;

import org.objectweb.asm.MethodVisitor;

public class GherkinMethodVisitor extends MethodVisitor {
    public GherkinMethodVisitor(int api) {
        super(api);
    }
}
