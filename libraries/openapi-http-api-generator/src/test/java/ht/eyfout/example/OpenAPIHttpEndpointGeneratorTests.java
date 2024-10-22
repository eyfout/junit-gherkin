package ht.eyfout.example;

import ht.eyfout.http.HttpEndpoint;
import ht.eyfout.openapi.http.generator.OpenAPIHttpEndpointGenerator;
import ht.eyfout.openapi.http.generator.Pair;
import ht.eyfout.http.openapi.generated.GjCGHttpEndpoint;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenAPIHttpEndpointGeneratorTests {

    @Test
    /**
     * Generates the data structures relative to {@link OpenAPIHttpEndpointGenerator} package, followed by generate/{namespace}.
     */
    void genCode() {
        URL petStore = OpenAPIHttpEndpointGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        URL supplyWarehouse = OpenAPIHttpEndpointGeneratorTests.class.getClassLoader().getResource("supply-warehouse.yml");
        File rootDir = new File(new File("").getAbsolutePath(), "/build/generated/classes");
        rootDir.mkdirs();
        OpenAPIHttpEndpointGenerator.generate(petStore.toString(), rootDir, "petstore");
        OpenAPIHttpEndpointGenerator.generate(supplyWarehouse.toString(), rootDir, "warehouse");

        File destination = new File(rootDir, GjCGHttpEndpoint.class.getPackageName().replace('.', File.separatorChar));
        assertTrue(new File(destination, "petstore").exists(), "petstore @" + destination.getAbsolutePath());
        assertTrue(new File(destination, "warehouse").exists(), "warehouse @" + destination.getAbsolutePath());
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
        URL petStore = OpenAPIHttpEndpointGeneratorTests.class.getClassLoader().getResource("pet-store.yml");
        List<Pair<String, byte[]>> petstore = OpenAPIHttpEndpointGenerator
                .codeGen(petStore.toString(), "teach")
                .generate(
                        it -> it.withDesc(asByte(GjCGHttpEndpoint.class)),
                        it -> it.rebrand(asByte(GjCGHttpEndpoint.RequestBuilder.class), false),
                        it -> it.rebrand(asByte(GjCGHttpEndpoint.HeaderParam.class), true),
                        it -> it.rebrand(asByte(GjCGHttpEndpoint.PathParam.class), true),
                        it -> it.rebrand(asByte(GjCGHttpEndpoint.QueryParam.class), true),
                        it -> it.rebrand(asByte(GjCGHttpEndpoint.Param.class), false)
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
