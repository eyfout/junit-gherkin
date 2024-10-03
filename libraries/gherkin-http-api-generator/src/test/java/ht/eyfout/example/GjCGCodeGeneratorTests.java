package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GjCGCodeGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GjCGCodeGeneratorTests {

    @Test
    /**
     * Generates the data structures relative to {@link GjCGCodeGenerator} package, followed by generate/{namespace}.
     */
    void generate_multiple_models_with_unique_namespace() {
        URL petStore = GjCGCodeGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        URL supplyWarehouse = GjCGCodeGeneratorTests.class.getClassLoader().getResource("supply-warehouse.yml");
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        GjCGCodeGenerator.generate(petStore, rootDir, "petstore");
        GjCGCodeGenerator.generate(supplyWarehouse, rootDir, "warehouse");

        File destination = new File(rootDir, GjCGCodeGenerator.class.getPackageName().replace('.', File.separatorChar));
        assertTrue(new File(destination, "generated/petstore").exists(), "petstore @" + destination.getAbsolutePath());
        assertTrue(new File(destination, "generated/warehouse").exists(), "warehouse @" + destination.getAbsolutePath());
    }
}
