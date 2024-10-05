package ht.eyfout.plugins.junit.jupiter.gherkin;

import ht.eyfout.junit.jupiter.gherkin.http.GherkinHttpAPIGenerationException;
import ht.eyfout.junit.jupiter.gherkin.http.GherkinHttpAPIGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

abstract class GherkinHttpCodeGenerationTask extends DefaultTask {

    @Inject
    public GherkinHttpCodeGenerationTask() {

    }

    @InputDirectory
    private Directory specification = getProject().getLayout().getProjectDirectory().dir("src/main/resources/junit-gherkin");
    @OutputDirectory
    private Provider<Directory> out = getProject().getLayout().getBuildDirectory().dir("generated/junit-gherkin");


    byte[] load(String klass) {
        try {
            return getClass().getClassLoader().getResourceAsStream(klass).readAllBytes();
        } catch (IOException e) {
            throw new GherkinHttpAPIGenerationException(klass, e);
        }
    }


    @TaskAction
    void action() {
        String pkg = "ht/eyfout/junit/jupiter/gherkin/http/generated/";
        specification.getAsFileTree().forEach(openAPI -> {
            GherkinHttpAPIGenerator.codeGen(openAPI.getAbsolutePath(), openAPI.getName())
                    .generate(
                            it -> it.rebrand(load(pkg + "GjCGRequestBuilder.class"), false),
                            it -> it.withDesc(load(pkg + "GjCGHttpAPI.class")),
                            it -> it.rebrand(load(pkg + "GjCGRequestBuilder$GjCGQueryParam.class"), true),
                            it -> it.rebrand(load(pkg + "GjCGRequestBuilder$GjCGPathParam.class"), true),
                            it -> it.rebrand(load(pkg + "GjCGRequestBuilder$GjCGHeaderParam.class"), true),
                            it -> it.rebrand(load(pkg + "GjCGRequestBuilder$Param.class"), false)
                    ).forEach(it -> {
                        int index = it.first().lastIndexOf('/');
                        File dir = new File(out.get().getAsFile(), it.first().substring(0, index));
                        dir.mkdirs();
                        File fs = new File(dir, it.first().substring(index + 1) + ".class");
                        try( FileOutputStream os = new FileOutputStream(fs)) {
                            fs.createNewFile();
                            os.write(it.second());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }

    public Directory getSpecification() {
        return specification;
    }

    public void setSpecification(Directory specification) {
        this.specification = specification;
    }

    public Provider<Directory> getOut() {
        return out;
    }

    public void setOut(Provider<Directory> out) {
        this.out = out;
    }
}
