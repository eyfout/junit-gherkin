package ht.eyfout.openapi.http.api;

import ht.eyfout.openapi.http.api.generated.GjCGHttpAPI;
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

    static private byte[] bytes(Class<?> klass) {
        try {
            return klass.getClassLoader().getResourceAsStream(klass.getName().replace('.', '/') + ".class").readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(klass.getName(), e);
        }
    }

    /**
     * Replace prefix in str with
     *
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
     * @param url
     * @param rootDir
     * @param namespace
     */
    static public void generate(String url, File rootDir, String namespace) {
        codeGen(url, namespace).generate(it -> {
                    it.setPrefix("GjCG");
                    Stream<Pair<String, byte[]>> parameters = Arrays.stream(GjCGHttpAPI.class.getDeclaredClasses()).flatMap(klass ->
                            it.rebrand(bytes(klass), klass.getSimpleName().toLowerCase().contains("param"))
                    );
                    return Stream.concat(it.withDesc(bytes(GjCGHttpAPI.class)), parameters);
                })
                .forEach(it -> {
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
            reader.accept(new HttpParamClassVisitor(Opcodes.ASM9, writer, parameterPairs, rename), ClassWriter.COMPUTE_FRAMES);
            return writer.toByteArray();
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public Stream<Pair<String, byte[]>> rebrand(byte[] template, boolean isParam) {
            return apis.stream().map(api -> {
                Function<String, String> rename = it -> replace(namespace, it, getPrefix(), api.id());
                ClassReader reader = new ClassReader(template);
                ClassWriter sink = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                HttpClassVisitor httpVisitor = new HttpClassVisitor(Opcodes.ASM9, sink, rename, (n, d, v) -> new HttpMethodVisitor(Opcodes.ASM7, v, rename));
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
                HttpClassVisitor visitor = new HttpClassVisitor(Opcodes.ASM9, sink, rename, (n, d, v) -> new HttpAPIMethodVisitor(Opcodes.ASM7, v, api, n, rename));
                reader.accept(visitor, ClassReader.EXPAND_FRAMES);
                return new Pair<>(visitor.name().get(), sink.toByteArray());
            });
        }

        @SafeVarargs
        public final Stream<Pair<String, byte[]>> generate(Function<CodeGenerator, Stream<Pair<String, byte[]>>>... consumer) {
            return Arrays.stream(consumer).flatMap(it -> it.apply(this));
        }

        public final Stream<Pair<String, byte[]>> generate(ClassLoader cl) {
            String api = "ht.eyfout.openapi.http.api.generated.GjCGHttpAPI";
            try {
                Class<?> apiClass = Class.forName(api, false, cl);
                Stream<Pair<String, byte[]>> apis = generate(it -> it.withDesc(readAllBytes(cl, apiClass.getName())));
                Stream<Pair<String, byte[]>> resources = Arrays.stream(apiClass.getDeclaredClasses()).flatMap(klass ->
                        generate(it -> it.rebrand(readAllBytes(cl, klass.getName()), klass.getSimpleName().toLowerCase().contains("param")))
                );
                return Stream.concat(apis, resources);
            } catch (ClassNotFoundException e) {
                throw new GherkinHttpAPIGenerationException(api, e);
            }
        }

        private byte[] readAllBytes(ClassLoader cl, String name) {
            try {
                return Objects.requireNonNull(cl.getResourceAsStream(name.replace('.', '/') + ".class")).readAllBytes();
            } catch (Throwable e) {
                throw new GherkinHttpAPIGenerationException(name, e);
            }
        }
    }
}
