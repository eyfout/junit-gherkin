package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.GherkinHttpAPIGenerator;
import ht.eyfout.junit.jupiter.gherkin.http.Pair;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGRequestBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GherkinHttpAPIGeneratorTests {

    @Test
    /**
     * Generates the data structures relative to {@link GherkinHttpAPIGenerator} package, followed by generate/{namespace}.
     */
    void genCode() {
        URL petStore = GherkinHttpAPIGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        URL supplyWarehouse = GherkinHttpAPIGeneratorTests.class.getClassLoader().getResource("supply-warehouse.yml");
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        GherkinHttpAPIGenerator.generate(petStore.toString(), rootDir, "petstore");
        GherkinHttpAPIGenerator.generate(supplyWarehouse.toString(), rootDir, "warehouse");

        File destination = new File(rootDir, GherkinHttpAPIGenerator.class.getPackageName().replace('.', File.separatorChar));
        assertTrue(new File(destination, "generated/petstore").exists(), "petstore @" + destination.getAbsolutePath());
        assertTrue(new File(destination, "generated/warehouse").exists(), "warehouse @" + destination.getAbsolutePath());
    }

    byte[] asByte(Class<?> klass) {
        try {
            return klass.getClassLoader()
                    .getResourceAsStream(klass.getName().replace('.', File.separatorChar) + ".class")
                    .readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void genCodeExplicitly() {
        URL petStore = GherkinHttpAPIGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        List<Pair<String, byte[]>> petstore = GherkinHttpAPIGenerator
                .codeGen(petStore.toString(), "teach")
                .generate(
                        it -> it.withDesc(asByte(GjCGHttpAPI.class)),
                        it -> it.rebrand(asByte(GjCGRequestBuilder.class), false),
                        it -> it.rebrand(asByte(GjCGRequestBuilder.GjCGHeaderParam.class), true),
                        it -> it.rebrand(asByte(GjCGRequestBuilder.GjCGPathParam.class), true),
                        it -> it.rebrand(asByte(GjCGRequestBuilder.GjCGQueryParam.class), true),
                        it -> it.rebrand(asByte(GjCGRequestBuilder.Param.class), false)
                ).toList();
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        petstore.forEach(it -> {
            int index = it.first().lastIndexOf('/');
            File dir = new File(rootDir, it.first().substring(0, index));
            dir.mkdirs();
            File fs = new File(dir, it.first().substring(index + 1) + ".class");
            try {
                fs.createNewFile();
                new FileOutputStream(fs).write(it.second());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
