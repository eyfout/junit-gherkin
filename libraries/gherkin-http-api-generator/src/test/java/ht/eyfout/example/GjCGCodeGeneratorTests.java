package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GjCGCodeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

public class GjCGCodeGeneratorTests {

    @Test
    void generate() {
        URL resource = GjCGCodeGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        GjCGCodeGenerator.generate(resource, rootDir, "rbs");
        GjCGCodeGenerator.generate(resource, rootDir, "chase");
    }
}
