package ht.eyfout.junit.jupiter.gherkin.http;

import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

final public class GherkinHttpAPIGenerator {
    public static String camelCase(String it) {
        return it.toUpperCase().charAt(0) + it.substring(1);
    }

    /**
     * Replace prefix in str with with
     * @param namespace
     * @param str
     * @param prefix
     * @param with
     * @return
     */
    static String replace(
            String namespace,
            String str,
            String prefix,
            String with) {
        if (str != null) {
            int index = str.indexOf(prefix);
            Character delimiter = null;
            if (index > 2) {
                delimiter = str.charAt(index - 1);
            }
            String newStr = str.replace(prefix, with);
            if (delimiter != null) {
                newStr = newStr.replace(delimiter + with, delimiter + namespace + delimiter + with);
            }
            return newStr;
        }
        return str;
    }

    /**
     *
     * @param url
     * @param rootDir
     * @param namespace
     */
    static public void generate(String url, File rootDir, String namespace) {
        codeGen(url, namespace).all().forEach(it -> {
            int index = it.first().lastIndexOf('/');
            File dir = new File(rootDir, it.first().substring(0, index));
            dir.mkdirs();
            File fs = new File(dir, it.first().substring(index + 1) + ".class");
            try {
                fs.createNewFile();
                new FileOutputStream(fs).write(it.second());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    static public CodeGenerator codeGen(String url, String namespace) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(namespace, "namespace");
        OpenAPI openAPI = new OpenAPIParser().readLocation(url, null, null).getOpenAPI();
        return new CodeGenerator(openAPI.getPaths(), namespace);
    }

    static public class CodeGenerator {
        private final String namespace;
        private final List<SwaggerAPI> apis;
        private String prefix = "GjCG";

        private CodeGenerator(Paths paths, String namespace) {
            this.namespace = namespace;
            apis = paths.entrySet().stream().parallel().flatMap(path ->
                    path.getValue().readOperationsMap().entrySet().stream().map(it ->
                            new SwaggerAPI(path.getKey(), it.getKey(), it.getValue()))).toList();
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        private static byte[] methodParams(String klass, byte[] content, SwaggerAPI api, Function<String, String> rename) {
            ClassReader reader = new ClassReader(content);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            Collection<Parameter> parameterPairs = new ArrayList<>();
            Optional.ofNullable(api.operation().getParameters()).ifPresent(params -> {
                List<Parameter> pairs = params.stream()
                        .filter(it -> it.getIn() != null && it.getName() != null)
                        .filter(it -> klass.contains(camelCase(it.getIn())))
                        .toList();
                parameterPairs.addAll(pairs);
            });
            reader.accept(new HttpParamClassVisitor(Opcodes.ASM7, writer, parameterPairs, rename), ClassWriter.COMPUTE_FRAMES);
            return writer.toByteArray();
        }

        private byte[] bytes(Class<?> klass) {
            try {
                return klass.getClassLoader().getResourceAsStream(klass.getName().replace('.', '/') + ".class").readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(klass.getName(), e);
            }
        }

        public Stream<Pair<String, byte[]>> all() {
            Stream<Pair<String, byte[]>> parameters = Arrays.stream(GjCGRequestBuilder.class.getDeclaredClasses()).flatMap(klass ->
                    generate(it -> it.rebrand(bytes(klass), true))
            );
            Stream<Pair<String, byte[]>> apis = Stream.concat(
                    generate(it -> rebrand(bytes(GjCGRequestBuilder.class), false)),
                    generate(it -> withDesc(bytes(GjCGHttpAPI.class)))
            );
            return Stream.concat(apis, parameters);
        }

        public Stream<Pair<String, byte[]>> rebrand(byte[] template, boolean isParam) {
            return apis.stream().map(api -> {
                Function<String, String> rename = it -> replace(namespace, it, prefix, api.id());
                ClassReader reader = new ClassReader(template);
                ClassWriter sink = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                HttpClassVisitor httpVisitor = new HttpClassVisitor(Opcodes.ASM7, sink, rename, (n, d, v) -> new HttpMethodVisitor(Opcodes.ASM7, v, rename));
                reader.accept(httpVisitor, ClassReader.EXPAND_FRAMES);
                String className = httpVisitor.name().get();
                if (isParam) {
                    return new Pair<>(className, methodParams(className, sink.toByteArray(), api, rename));
                } else {
                    return new Pair<>(className, sink.toByteArray());
                }
            });
        }

        public Stream<Pair<String, byte[]>> withDesc(byte[] template) {
            return apis.stream().map(api -> {
                Function<String, String> rename = it -> replace(namespace, it, prefix, api.id());
                ClassReader reader = new ClassReader(template);
                ClassWriter sink = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                HttpClassVisitor visitor = new HttpClassVisitor(Opcodes.ASM7, sink, rename, (n, d, v) -> new HttpAPIMethodVisitor(Opcodes.ASM7, v, api, n, rename));
                reader.accept(visitor, ClassReader.EXPAND_FRAMES);
                return new Pair<>(visitor.name().get(), sink.toByteArray());
            });
        }

        @SafeVarargs
        public final Stream<Pair<String, byte[]>> generate(Function<CodeGenerator, Stream<Pair<String, byte[]>>>... consumer) {
            return Arrays.stream(consumer).flatMap(it -> it.apply(this));
        }
    }
}
