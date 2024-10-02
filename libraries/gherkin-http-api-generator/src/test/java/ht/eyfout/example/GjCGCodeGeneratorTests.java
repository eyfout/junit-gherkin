package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GjCGCodeGenerator;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class GjCGCodeGeneratorTests {

    @Test
    void generate() {
        URL resource = GjCGCodeGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        GjCGCodeGenerator.generate(resource);

    }
}
