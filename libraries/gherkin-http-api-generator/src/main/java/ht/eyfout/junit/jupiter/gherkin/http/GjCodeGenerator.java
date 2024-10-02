package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GjCodeGenerator {
    public static int API = Opcodes.ASM7;

    private static Map<Class<?>, Class<?>> methodVisitors() {
        Map<Class<?>, Class<?>> visitors = new HashMap<>();
        visitors.put(GjCGHttpAPI.class, GjCGMethodVisitor.class);
        return visitors;
    }

    static public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        Map<Class<?>, ClassReader> methods = new HashMap<>();

        final Class<GjCGHttpAPI> api = GjCGHttpAPI.class;
        List<Operation> ops = paths.values().stream()
                .flatMap(it -> it.readOperations().stream()).toList();
        GjCGClassVisitor classVisitor = new GjCGClassVisitor(API, List.of(ops.get(0)), api, new GjCGMethodVisitor(API));
        ClassReader classReader = getOrDefault(methods, api, GjCGClassVisitor::asClassReader);
        classReader.accept(classVisitor, Opcodes.V1_5);

        classVisitor.write((node, klass) -> {

            File f = new File(new File("").getAbsolutePath(), "/build/" + klass.getSimpleName().replace(GjCGClassVisitor.PREFIX, node.name()) + ".class");
            try {
                f.createNewFile();
                new FileOutputStream(f).write(((ClassWriter) node.klass()).toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return f;
        }).toList();
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
