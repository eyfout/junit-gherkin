package ht.eyfout.junit.jupiter.gherkin.http;

import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.function.Function;

class HttpClassVisitor extends ClassVisitor {

    private final Function<String, String> rebrand;
    private final TriFunction<String, String, MethodVisitor, MethodVisitor> mv;

    public HttpClassVisitor(int api,
                            ClassVisitor sink,
                            Function<String, String> rebrand,
                            TriFunction<String, String, MethodVisitor, MethodVisitor> methodVisitor) {
        super(api, sink);
        this.rebrand = rebrand;
        this.mv = methodVisitor;
    }


    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, rename(descriptor), visible);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(rename(descriptor), visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, rename(name), rename(signature), rename(superName), rename(interfaces));
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return mv.apply(rename(name), rename(descriptor), super.visitMethod(access, rename(name), rename(descriptor), rename(signature), rename(exceptions)));
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(rename(name), rename(outerName), rename(innerName), access);
    }

    @Override
    public void visitNestMember(String nestMember) {
        super.visitNestMember(rename(nestMember));
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(rename(owner), rename(name), rename(descriptor));
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, rename(name), rename(descriptor), rename(signature), value);
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(rename(source), rename(debug));
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(rename(name), access, version);
    }

    @Override
    public void visitNestHost(String nestHost) {
        super.visitNestHost(rename(nestHost));
    }

    @Override
    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return super.visitRecordComponent(rename(name), rename(descriptor), rename(signature));
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(rename(permittedSubclass));
    }

    private <R> R rename(R it) {
        if (it instanceof String) {
            return (R) rebrand.apply((String) it);
        } else if (it instanceof String[]) {
            return (R) Arrays.stream(((String[]) it)).map(this::rename).toArray(String[]::new);
        }
        return it;
    }
}
