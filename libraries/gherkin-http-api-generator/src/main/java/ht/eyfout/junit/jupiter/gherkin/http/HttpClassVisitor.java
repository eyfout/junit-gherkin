package ht.eyfout.junit.jupiter.gherkin.http;

import org.apache.commons.lang3.function.TriFunction;
import org.objectweb.asm.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

class HttpClassVisitor extends ClassVisitor {

    private final Function<String, String> rebrand;
    private final TriFunction<String, String, MethodVisitor, MethodVisitor> methodVisitor;

    public HttpClassVisitor(int api,
                            ClassVisitor source,
                            ClassVisitor sink,
                            Function<String, String> rebrand,
                            TriFunction<String, String, MethodVisitor, MethodVisitor> methodVisitor) {
        super(api, sink);
        this.rebrand = rebrand;
        this.methodVisitor = methodVisitor;
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        invoke(method("visitAttribute", Attribute.class), attribute);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return invoke(method("visitTypeAnnotation", int.class, TypePath.class, String.class, boolean.class),
                typeRef, typePath, descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return invoke(method("visitAnnotation", String.class, boolean.class), descriptor, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        invoke(method("visit", int.class, int.class, String.class, String.class, String.class, String[].class), version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return methodVisitor.apply(rename(name), rename(descriptor), cv.visitMethod(access, rename(name), rename(descriptor), rename(signature), exceptions));
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        invoke(method("visitInnerClass", String.class, String.class, String.class, int.class), name, outerName, innerName, access);
    }

    @Override
    public void visitNestMember(String nestMember) {
        invoke(method("visitNestMember", String.class), nestMember);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        invoke(method("visitOuterClass", String.class, String.class, String.class), owner, name, descriptor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return invoke(method("visitField", int.class, String.class, String.class, String.class, Object.class), access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        invoke(method("visitEnd"));
    }

    @Override
    public void visitSource(String source, String debug) {
        invoke(method("visitSource", String.class, String.class), source, debug);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        return invoke(method("visitModule", String.class, int.class, String.class), name, access, version);
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        invoke(method("visitPermittedSubclass"));
    }

    private Method method(String name, Class<?>... types) {
        try {
            return ClassVisitor.class.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <R> R invoke(Method method, Object... arguments) {
        try {
            return (R) method.invoke(cv, Arrays.stream(arguments).map(this::rename).toArray());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object rename(Object it) {
        if (it instanceof String) {
            return rebrand.apply((String) it);
        }
        return it;
    }

    private String rename(String it) {
        return rebrand.apply(it);
    }
}
