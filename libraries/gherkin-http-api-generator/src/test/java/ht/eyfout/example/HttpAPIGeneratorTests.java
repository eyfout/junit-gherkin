package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GjCodeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

public class HttpAPIGeneratorTests {

    @Test
    void generate() {
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        GjCodeGenerator.generate(resource);
    }
}
