package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.CodeGenerator;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class HttpAPIGeneratorTests {

    @Test
    void generate() {
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        CodeGenerator.generate(resource);

    }


}
