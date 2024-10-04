package ht.eyfout.plugins.junit.jupiter.gherkin;

import ht.eyfout.junit.jupiter.gherkin.http.GherkinHttpCodeGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

import java.io.File;

abstract class GherkinHttpCodeGenerationTask extends DefaultTask {
    @Inject
    public GherkinHttpCodeGenerationTask(){

    }

    @InputFile
    private File specification;
    @OutputDirectory
    private File out;

    @TaskAction
    void action() {
        GherkinHttpCodeGenerator.generate(specification.getAbsolutePath(), out, "pets");
        System.out.println("Yes, I am here");
    }

    public File getSpecification() {
        return specification;
    }

    public void setSpecification(File specification) {
        this.specification = specification;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }
}
