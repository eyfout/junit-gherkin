package ht.eyfout.junit.jupiter.api.http;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpAPIGenerator {
    private static final Type STRING = Type.getType(String.class);
    private static final Type CONSUMER = Type.getType(Consumer.class);
    private static final Type HTTP_API_REQUEST_BUILDER = Type.getType(HttpAPIRequestBuilder.class);
    private static final Type HTTP_API = Type.getType(HttpAPI.class);
    private static final Type OBJECT = Type.getType(Object.class);

    private static String className(String name) {
        return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
    }

    private static final Collection<String> IN = List.of(className("path"), className("query"), className("header"));

    private final String pkg = "ht/eyfout/junit/jupiter/http/generated/";


    void example() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
                "pkg/Comparable", null, "java/lang/Object",
                new String[]{"pkg/Mesurable"});
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I",
                null, -1).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I",
                null, 0).visitEnd();
        cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I",
                null, 1).visitEnd();
        cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
        cw.visitEnd();
        byte[] b = cw.toByteArray();
        new String(b, StandardCharsets.UTF_8);
    }

    void httpRequestBuilder(Operation op) {
        Map<String, List<Parameter>> params = new HashMap<>();
        Optional.ofNullable(op.getParameters()).ifPresent(parameters -> {
            params.putAll( parameters
                    .stream()
                    .collect(Collectors.groupingBy(it -> className(it.getIn().toLowerCase()))));
        });

        final String name = pkg + className(op.getOperationId());
        final ClassWriter builderWriter = new ClassWriter(0);
        final String builderClassName = name + "RequestBuilder";
        builderWriter.visit(Opcodes.V1_5,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                builderClassName,
                null,
                OBJECT.getInternalName(),
                new String[]{HTTP_API_REQUEST_BUILDER.getInternalName()});
        IN.forEach(in -> {
            parameter(builderClassName, in, params.getOrDefault(in, Collections.emptyList()));
            builderWriter.visitMethod(Opcodes.ACC_PUBLIC,
                    in.toLowerCase(),
                    Type.getMethodDescriptor(Type.getType(builderClassName), CONSUMER),
                    null,
                    null);
        });
        builderWriter.visitEnd();

        final ClassWriter apiWriter = new ClassWriter(0);
        final String httpAPIClassName = name + "HttpAPI";

    }

    void parameter(String builder, String in, List<Parameter> parameters) {
        ClassWriter writer = new ClassWriter(0);
        String klass = builder + "$" + in;
        writer.visit(Opcodes.V1_5,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                klass,
                null,
                OBJECT.getInternalName(),
                new String[]{HTTP_API_REQUEST_BUILDER.getInternalName()});
        parameters.forEach(it -> {
            writer.visitMethod(Opcodes.ACC_PUBLIC,
                    "set" + className(it.getName()),
                    Type.getMethodDescriptor(Type.getType(klass), CONSUMER),
                    null,
                    null);
        });
    }


    public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        paths.forEach((path, item) -> {
            item.readOperations().forEach(this::httpRequestBuilder);
        });
    }
}
