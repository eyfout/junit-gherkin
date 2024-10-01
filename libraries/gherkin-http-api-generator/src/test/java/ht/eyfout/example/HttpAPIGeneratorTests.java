package ht.eyfout.example;

import ht.eyfout.junit.jupiter.http.HttAPICodeGenerator;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class HttpAPIGeneratorTests {

    @Test
    void generate() {
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        HttAPICodeGenerator.generate(resource);
    }
}
