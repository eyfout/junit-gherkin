package ht.eyfout.junit.jupiter.http;

import io.swagger.v3.oas.models.Operation;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class GherkinClassVisitor extends ClassVisitor {

    private final List<? extends Map.Entry<Operation, GherkinNode>> reference;

    public GherkinClassVisitor(int api, Collection<Operation> op, Class<?> klass) {
        super(api);
        reference = op.stream().map(it -> toEntry(api, it)).toList();
    }

    private static Map.Entry<Operation, GherkinNode> toEntry(int api, Operation op) {
        return new AbstractMap.SimpleEntry<>(op, new GherkinNode(new ClassNode(api), Take2.classCase(op.getOperationId()), new ArrayList<>()));
    }

    public <R> Stream<R> write(BiFunction<ClassNode, MethodNode, R> func) {
        return reference.stream().map(ref -> {
            return func.apply(ref.getValue().klass, null);
        });
    }

    private Method method(String name, Class<?>... parameterTypes) {
        try {
            return ClassVisitor.class.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void forEach(Method method, Object... values) {
        reference.forEach(op -> {
            Object[] replacements = Arrays.stream(values).map(value -> {
                if (value instanceof String) {
                    return Take2.replace((String) value, op.getValue().name);
                } else {
                    return value;
                }
            }).toArray(Object[]::new);
            try {
                method.invoke(op.getValue().klass, replacements);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        forEach(method("visit", int.class, int.class, String.class, String.class, String.class, String[].class), version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        forEach(method("visitInnerClass", String.class, String.class, String.class, int.class), name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        forEach(method("visitModule", String.class, int.class, String.class), name, access, version);
        return super.visitModule(name, access, version);
    }

    @Override
    public void visitEnd() {
        forEach(method("visitEnd"));
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        forEach(method("visitField", int.class, String.class, String.class, String.class, Object.class), access, name, descriptor, signature, value);
        return super.visitField(access, name, descriptor, signature, value);
    }

    record GherkinNode(ClassNode klass, String name, List<GherkinClassVisitor> inner) {
    }
}
