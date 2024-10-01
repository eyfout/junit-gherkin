package ht.eyfout.junit.jupiter.http;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpAPIGenerator {
    private static final Type STRING = Type.getType(String.class);
    private static final Type CONSUMER = Type.getType(Consumer.class);
    private static final Type OBJECT = Type.getType(Object.class);
    private static final Collection<String> IN = List.of(className("path"), className("query"), className("header"));
    private final String ns = "ht.eyfout.junit.jupiter.http.generated";

    private static String className(String name) {
        return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
    }

    Stream<ClassTemplate> httpRequestBuilder(Operation op) {

        Map<String, List<Parameter>> parameters = new HashMap<>();
        Optional.ofNullable(op.getParameters()).ifPresent(params -> {
            parameters.putAll(params
                    .stream()
                    .collect(Collectors.groupingBy(it -> className(it.getIn().toLowerCase()))));
        });


        final String name = className(op.getOperationId());
        ClassTemplate requestBuilder = new ClassTemplate.RequestBuilderClassTemplate(ns, name);
        ClassTemplate api = new ClassTemplate.HttpAPIClassTemplate(ns, name);
        List<ClassTemplate> meta = new ArrayList<>();
        meta.add(requestBuilder);
        meta.add(api);

        IN.forEach(in -> {
            ClassTemplate param = new ClassTemplate.HttpParamClassTemplate(requestBuilder, in);
            meta.add(param);
            paramSetterMethods(param, parameters.getOrDefault(in, Collections.emptyList())).forEach(it -> {
                it.accept(param.asmNode());
            });
            param.asmNode().visitEnd();


            new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                    in.toLowerCase() + "Param",
                    Type.getMethodDescriptor(requestBuilder.type, CONSUMER),
                    null,
                    null).accept(requestBuilder.asmNode());

        });
        api.asmNode().visitEnd();
        requestBuilder.asmNode().visitEnd();
        return meta.stream();
    }

    Collection<MethodNode> paramSetterMethods(ClassTemplate klass, List<Parameter> parameters) {
        return parameters.stream().map(it -> {
            MethodNode node = new MethodNode(Opcodes.V1_5);
            node.name = "set" + className(it.getName());
            node.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL;
            node.parameters.add(new ParameterNode("consumer", Opcodes.ACC_PUBLIC));
            node.desc = "(" + STRING.getInternalName() + ")";


            return node;
        }).toList();
    }

    public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        paths.forEach((path, item) -> {
            item.readOperations().stream()
                    .flatMap(this::httpRequestBuilder)
                    .forEach(it -> {
                        try {


                            ClassWriter writer = new ClassWriter(0);
                            it.asmNode().accept(writer);
                            System.out.write(writer.toByteArray());
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }
}
