package ht.eyfout.example;

import ht.eyfout.junit.jupiter.api.http.HttpAPIGenerator;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class HttpAPIGeneratorTests {
    @Test
    void generate() {
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
        new HttpAPIGenerator().generate(resource);
    }
}
