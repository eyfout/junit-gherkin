package ht.eyfout.junit.jupiter.http;

import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitHttpAPI;
import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitRequestBuilder;
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
    static final String PREFIX = "GherkinJUnit";
    private final List<? extends Map.Entry<Operation, GherkinNode>> operations;
    private final Class<?> klass;

    public GherkinClassVisitor(int api, Collection<Operation> op, Class<?> klass) {
        super(api);
        operations = op.stream().map(it -> toEntry(api, it)).toList();
        this.klass = klass;
    }

    private static String replacement(Class<?> klass, String alt) {
        return klass.getSimpleName().replace(PREFIX, alt);
    }

    private static String replace(String orig, Class<?> klass, String alt) {
        return orig.replace(klass.getSimpleName(), replacement(klass, alt));
    }

    private static String replace(String orig, String alt) {
        if (Objects.isNull(orig)) {
            return orig;
        } else {
            String value = replace(
                    replace(
                            orig,
                            GherkinJUnitRequestBuilder.class,
                            alt),
                    GherkinJUnitHttpAPI.class,
                    alt);
            return replace(
                    replace(
                            replace(value, GherkinJUnitRequestBuilder.GherkinJUnitPathParam.class, alt),
                            GherkinJUnitRequestBuilder.GherkinJUnitQueryParam.class,
                            alt),
                    GherkinJUnitRequestBuilder.GherkinJUnitHeaderParam.class,
                    alt);
        }
    }

    private static String classCase(String name) {
        return name.toUpperCase().charAt(0) + name.substring(1);
    }

    private static Map.Entry<Operation, GherkinNode> toEntry(int api, Operation op) {
        return new AbstractMap.SimpleEntry<>(op, new GherkinNode(new ClassNode(api), classCase(op.getOperationId()), new ArrayList<>()));
    }

    public <R> Stream<R> write(BiFunction<ClassNode, MethodNode, R> func) {
        return operations.stream().map(ref -> {
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
        operations.forEach(op -> {
            Object[] replacements = Arrays.stream(values).map(value -> {
                if (value instanceof String) {
                    return replace((String) value, op.getValue().name);
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
