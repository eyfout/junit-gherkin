package ht.eyfout.junit.jupiter.http;

import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitHttpAPI;
import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitRequestBuilder;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Take2 {

    static final String PREFIX = "GherkinJUnit";

    static String replacement(Class<?> klass, String alt) {
        return klass.getSimpleName().replace(PREFIX, alt);
    }

    static String replace(String orig, Class<?> klass, String alt) {
        return orig.replace(klass.getSimpleName(), replacement(klass, alt));
    }

    static String replace(String orig, String alt) {
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

    static String classCase(String name) {
        return name.toUpperCase().charAt(0) + name.substring(1);
    }

    static public void generate(URL in) {
        OpenAPI openAPI = new OpenAPIParser().readLocation(in.toString(), null, null)
                .getOpenAPI();
        Paths paths = openAPI.getPaths();


        final Class<GherkinJUnitHttpAPI> api = GherkinJUnitHttpAPI.class;
        GherkinClassVisitor rewrite = new GherkinClassVisitor(Opcodes.ASM4, paths.values().stream()
                .flatMap(it -> it.readOperations().stream()).toList(), api);

        try {
            ClassReader classReader = new ClassReader(api.getSimpleName());
            classReader.accept(rewrite, Opcodes.V1_5);
            rewrite.write((klass, method) -> {
                ClassWriter writer = new ClassWriter(Opcodes.ASM4);
                klass.accept(writer);
                return writer;
            }).map(it -> new String(it.toByteArray())).forEach(it -> {
                System.out.println(it);
                System.out.println();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
