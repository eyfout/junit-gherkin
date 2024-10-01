package ht.eyfout.junit.jupiter.http;

import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitHttpAPI;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HttAPICodeGenerator {

    static public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        Map<Class<?>, ClassReader> methods = new HashMap<>();

        final Class<GherkinJUnitHttpAPI> api = GherkinJUnitHttpAPI.class;
        GherkinClassVisitor classVisitor = new GherkinClassVisitor(Opcodes.ASM4, paths.values().stream()
                .flatMap(it -> it.readOperations().stream()).toList(), api);
        ClassReader classReader = getOrDefault(methods, api, HttAPICodeGenerator::asClassReader);
        classReader.accept(classVisitor, Opcodes.V1_5);
        classVisitor.write((node, klass) -> {
            ClassWriter writer = new ClassWriter(Opcodes.ASM4);
//            GherkinMethodVisitor methodVisitor = new GherkinMethodVisitor(Opcodes.ASM4);
//            getOrDefault(methods, klass, HttAPICodeGenerator::asClassReader).accept(methodVisitor);
            node.accept(writer);
            return writer;
        }).map(it -> new String(it.toByteArray())).forEach(it -> {
            System.out.println(it);
            System.out.println();
        });
    }

    private static ClassReader asClassReader(Class<?> klass) {
        try {
            return new ClassReader(klass.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <K, V> V getOrDefault(Map<K, V> map, K key, Function<K, V> defaultValue) {
        V value = map.get(key);
        if (value == null) {
            value = defaultValue.apply(key);
            map.put(key, value);
        }
        return value;
    }
}
