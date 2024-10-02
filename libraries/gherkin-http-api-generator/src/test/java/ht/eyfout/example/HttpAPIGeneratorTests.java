package ht.eyfout.example;

import ht.eyfout.junit.jupiter.gherkin.http.CodeGenerator;
import ht.eyfout.junit.jupiter.gherkin.http.GjCGClassVisitor;
import ht.eyfout.junit.jupiter.gherkin.http.GjCodeGenerator;
import ht.eyfout.junit.jupiter.gherkin.http.generated.GjCGHttpAPI;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class HttpAPIGeneratorTests {

    @Test
    void generate() {
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
//        GjCodeGenerator.generate(resource);
        CodeGenerator.generate(resource);

    }

    @Test
    void asIs() throws IOException {
        Class<?> klass = GjCGHttpAPI.class;
        int lastIndex = klass.getName().lastIndexOf(".");
        File f = new File(new File("").getAbsolutePath(), "/build/classes/java/main");
        f = new File(f, klass.getName().replace('.', '/').substring(0, lastIndex));
        f.mkdirs();
        f = new File(f, klass.getName().substring(lastIndex + 1).replace(GjCGClassVisitor.PREFIX, "Acme") + ".class");

        f.createNewFile();
        new FileOutputStream(f).write(new ClassWriter(new ClassReader(klass.getName()), Opcodes.V1_5).toByteArray());

    }

}
