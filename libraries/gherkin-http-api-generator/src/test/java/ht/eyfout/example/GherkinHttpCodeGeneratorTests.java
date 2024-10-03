package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GherkinHttpCodeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GherkinHttpCodeGeneratorTests {

    @Test
    /**
     * Generates the data structures relative to {@link GherkinHttpCodeGenerator} package, followed by generate/{namespace}.
     */
    void generate_multiple_models_with_unique_namespace() {
        URL petStore = GherkinHttpCodeGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        URL supplyWarehouse = GherkinHttpCodeGeneratorTests.class.getClassLoader().getResource("supply-warehouse.yml");
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        GherkinHttpCodeGenerator.generate(petStore, rootDir, "petstore");
        GherkinHttpCodeGenerator.generate(supplyWarehouse, rootDir, "warehouse");

        File destination = new File(rootDir, GherkinHttpCodeGenerator.class.getPackageName().replace('.', File.separatorChar));
        assertTrue(new File(destination, "generated/petstore").exists(), "petstore @" + destination.getAbsolutePath());
        assertTrue(new File(destination, "generated/warehouse").exists(), "warehouse @" + destination.getAbsolutePath());
    }
}
