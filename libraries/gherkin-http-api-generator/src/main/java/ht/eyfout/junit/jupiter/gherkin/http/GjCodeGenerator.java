package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
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

        List<SwaggerAPI> swaggerAPIS = paths.entrySet().stream().flatMap(path ->
                path.getValue().readOperationsMap().entrySet().stream().map(it -> new SwaggerAPI(path.getKey(), it.getKey(), it.getValue()))
        ).toList();


        final Class<GjCGHttpAPI> api = GjCGHttpAPI.class;

        GjCGClassVisitor classVisitor = new GjCGClassVisitor(API, List.of(swaggerAPIS.get(0)), api);
        ClassReader classReader = getOrDefault(methods, api, GjCGClassVisitor::asClassReader);
        classReader.accept(classVisitor, Opcodes.V1_5);

        classVisitor.write((node, klass) -> {
            int lastIndex = klass.getName().lastIndexOf(".");

            File f = new File(new File("").getAbsolutePath(), "/build/classes/java/main");
            f = new File(f, klass.getName().replace('.', '/').substring(0, lastIndex));
            f.mkdirs();
            f = new File(f, klass.getName().substring(lastIndex + 1).replace(GjCGClassVisitor.PREFIX, node.name()) + ".class");
            try {
                f.createNewFile();
                new FileOutputStream(f).write(((ClassWriter) node.cv()).toByteArray());
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
