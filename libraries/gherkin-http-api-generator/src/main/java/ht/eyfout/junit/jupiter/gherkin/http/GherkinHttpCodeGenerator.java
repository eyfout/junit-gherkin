package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class GherkinHttpCodeGenerator {
    private static ClassReader asClassReader(Class<?> klass) {
        try {
            return new ClassReader(klass.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String camelCase(String it) {
        return it.toUpperCase().charAt(0) + it.substring(1);
    }

    static String replace(String namespace, String it, String other) {
        if (it != null) {
            int index = it.indexOf("GjCG");
//            String str = it.replace("GjCG", other);
            Character delimiter = null;
            if (index > 2) {
                delimiter = it.charAt(index - 1);
            }
            String str = it.replace("GjCG", other);
            if (delimiter != null) {
                str = str.replace(delimiter + other, delimiter + namespace + delimiter + other);
            }
            return str;
        }
        return it;
    }

    static public void generate(URL in, File rootDir, String namespace) {
        Objects.requireNonNull(in);
        Objects.requireNonNull(rootDir);
        Objects.requireNonNull(namespace);

        final String ns = namespace.toLowerCase();
        Set<Class<?>> paramType = Arrays.stream(GjCGRequestBuilder.class.getDeclaredClasses()).collect(Collectors.toSet());
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();
        paths.entrySet().stream().parallel().flatMap(path ->
                path.getValue().readOperationsMap().entrySet().stream().map(it -> new SwaggerAPI(path.getKey(), it.getKey(), it.getValue()))
        ).forEach(api -> {
            final Function<String, String> rename = it -> replace(ns, it, api.id());
            Stream.of(GjCGHttpAPI.class, GjCGRequestBuilder.class)
                    .flatMap(it -> Stream.concat(Stream.of(it), Arrays.stream(it.getDeclaredClasses())))
                    .forEach(klass -> {
                        int dot = klass.getName().lastIndexOf('.');
                        String fName = rename.apply(klass.getName().substring(dot + 1)) + ".class";
                        byte[] content = generate(klass, api, rename);
                        if (paramType.contains(klass)) {
                            content = with(klass, content, api, rename);
                        }
                        createFile(rootDir, ns, fName, klass, content);
                    });
        });
    }

    private static byte[] with(Class<?> klass, byte[] content, SwaggerAPI api, Function<String, String> rename) {
        ClassReader reader = new ClassReader(content);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        Collection<Parameter> parameterPairs = new ArrayList<>();
        Optional.ofNullable(api.operation().getParameters()).ifPresent(params -> {
            List<Parameter> pairs = params.stream()
                    .filter(it -> it.getIn() != null && it.getName() != null)
                    .filter(it -> klass.getName().contains(camelCase(it.getIn())))
                    .toList();
            parameterPairs.addAll(pairs);
        });
        reader.accept(new HttpParamClassVisitor(Opcodes.ASM7, writer, parameterPairs, rename), ClassWriter.COMPUTE_FRAMES);
        return writer.toByteArray();
    }

    private static void createFile(File rootDir, String namespace, String fName, Class<?> klass, byte[] content) {
        int dot = klass.getName().lastIndexOf(".");
        File packageDir = new File(new File(rootDir, klass.getName().substring(0, dot).replace('.', File.separatorChar) + File.separator), namespace);
        packageDir.mkdirs();
        File file = new File(packageDir, fName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generate(Class<?> klass, SwaggerAPI api, Function<String, String> rename) {
        return generate(klass, (source, sink) -> new HttpClassVisitor(Opcodes.ASM7, source, sink, rename,
                (name, descriptor, it) -> {
                    if (klass == GjCGHttpAPI.class) {
                        return new HttpAPIMethodVisitor(Opcodes.ASM7, null, it, api, name, rename);
                    } else {
                        return new HttpMethodVisitor(Opcodes.ASM7, null, it, rename);
                    }
                })
        );
    }

    private static byte[] generate(Class<?> klass,
                                   BiFunction<ClassVisitor, ClassVisitor, ClassVisitor> visitor) {
        ClassReader reader = asClassReader(klass);
        ClassWriter source = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassWriter sink = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        reader.accept(visitor.apply(source, sink), ClassReader.EXPAND_FRAMES);
        return sink.toByteArray();
    }

}
