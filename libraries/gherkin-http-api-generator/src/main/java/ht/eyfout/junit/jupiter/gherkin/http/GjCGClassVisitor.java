package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
import io.swagger.v3.oas.models.Operation;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class GjCGClassVisitor extends ClassVisitor {
    static final String PREFIX = "GjCG";
    final Class<?> klass;
    private final List<? extends Map.Entry<Operation, GherkinNode>> operations;
    private final Set<Type> visited = new HashSet<>();
    private final MethodVisitor methodVisitors;

    public GjCGClassVisitor(int api,
                            Collection<Operation> op,
                            Class<?> klass,
                            MethodVisitor methodVisitors) {
        super(api, new ClassWriter(asClassReader(klass), Opcodes.V1_5));
        List<GjCGClassVisitor> declared = Arrays.stream(klass.getDeclaredClasses()).map(inner -> {
            visited.add(Type.getType(inner));
            return new GjCGClassVisitor(api, op, inner, methodVisitors);
        }).collect(Collectors.toList());
        operations = op.stream().map(it -> toEntry(api, it, declared)).toList();
        visited.add(Type.getType(klass));
        this.methodVisitors = methodVisitors;
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
                            GjCGRequestBuilder.class,
                            alt),
                    GjCGHttpAPI.class,
                    alt);
            return replace(
                    replace(
                            replace(value, GjCGRequestBuilder.GjCGPath.class, alt),
                            GjCGRequestBuilder.GjCGQuery.class,
                            alt),
                    GjCGRequestBuilder.GjCGHeader.class,
                    alt);
        }
    }

    private static String classCase(String name) {
        return name.toUpperCase().charAt(0) + name.substring(1);
    }

    private static Map.Entry<Operation, GherkinNode> toEntry(int api, Operation op, List<GjCGClassVisitor> visitors) {
        visitors.forEach(it -> {
            asClassReader(it.klass).accept(it, Opcodes.V1_5);
        });
        return new AbstractMap.SimpleEntry<>(op,
                new GherkinNode(new ClassWriter(api),
                        classCase(op.getOperationId()),
                        visitors)
        );
    }

    public static ClassReader asClassReader(Class<?> klass) {
        try {
            return new ClassReader(klass.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Stream<GjCGClassVisitor> recurse() {
        return Stream.concat(Stream.of(this),
                operations.stream().flatMap(it -> it.getValue().inner.stream().flatMap(GjCGClassVisitor::recurse)));

    }

    public <R> Stream<R> write(BiFunction<GherkinNode, Class<?>, R> func) {
        return recurse().flatMap(visitor ->
                visitor.operations.stream().map(it ->
                        func.apply(it.getValue(), visitor.klass)
                )
        );
    }

    private Method method(String name, Class<?>... parameterTypes) {
        try {
            return ClassVisitor.class.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] convert(Object[] values, String substring) {
        return Arrays.stream(values).map(value -> {
            if (value instanceof String) {
                return replace((String) value, substring);
            } else {
                return value;
            }
        }).toArray(Object[]::new);
    }

    private void forEachOperation(Method method, Object... values) {
        forEachOperation((operation, node) -> {
            try {
                method.invoke(node.klass, convert(values, node.name));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        forEachOperation(method("visit", int.class, int.class, String.class, String.class, String.class, String[].class), version, access, name, signature, superName, interfaces);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        forEachOperation(method("visitInnerClass", String.class, String.class, String.class, int.class), name, outerName, innerName, access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public ModuleVisitor visitModule(String name, int access, String version) {
        forEachOperation(method("visitModule", String.class, int.class, String.class), name, access, version);
        return super.visitModule(name, access, version);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        forEachOperation(method("visitAttribute", Attribute.class), attribute);
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        forEachOperation(method("visitEnd"));
        super.visitEnd();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        forEachOperation(method("visitField", int.class, String.class, String.class, String.class, Object.class), access, name, descriptor, signature, value);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitSource(String source, String debug) {
        forEachOperation(method("visitSource", String.class, String.class), source, debug);
        super.visitSource(source, debug);
    }

    @Override
    public void visitNestMember(String nestMember) {
        forEachOperation(method("visitNestMember", String.class), nestMember);
        super.visitNestMember(nestMember);
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        forEachOperation(method("visitOuterClass", String.class, String.class, String.class), owner, name, descriptor);
        super.visitOuterClass(owner, name, descriptor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        Type methodType = Type.getMethodType(descriptor);
        forType(methodType.getReturnType());
        forType(methodType.getArgumentTypes());
        forEachOperation((operation, node) -> {
            MethodNode mn = new MethodNode(access, name, replace(descriptor, node.name), replace(signature, node.name), exceptions);
            if(name.equals("getDescription")){
                mn.visitCode();
                mn.visitLdcInsn(operation.getDescription());
                mn.visitInsn(Opcodes.RETURN);
                mn.visitEnd();
            }


            mn.accept(node.klass);
        });

        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        visitor.visitCode();
        return visitor;
    }

    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        forEachOperation(method("visitPermittedSubclass", String.class), permittedSubclass);
        super.visitPermittedSubclass(permittedSubclass);
    }

    private void forEachOperation(BiConsumer<Operation, GherkinNode> consumer) {
        operations.forEach(it -> consumer.accept(it.getKey(), it.getValue()));
    }

    private void forType(Type... type) {
        Arrays.stream(type).filter(it -> !this.visited.contains(it)).forEach(it -> {
            this.visited.add(it);
            try {
                forClass(Class.forName(it.getClassName()));
            } catch (ClassNotFoundException e) {
                //intentionally left blank
            }
        });
    }

    private void forClass(Class<?> klass) {
        if (klass.getPackage() == this.klass.getPackage()) {
            List<Operation> op = this.operations.stream().map(Map.Entry::getKey).toList();
            forEachOperation((operation, node) -> {
                GjCGClassVisitor ref = new GjCGClassVisitor(api, op, klass, this.methodVisitors);
                node.inner.add(ref);
                asClassReader(klass).accept(ref, Opcodes.V1_5);
            });
        }
    }

    record GherkinNode(ClassVisitor klass, String name, List<GjCGClassVisitor> inner) {
    }
}
