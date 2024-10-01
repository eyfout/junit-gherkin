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

public class HttAPICodeGenerator {

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
