package ht.eyfout.plugins.junit.jupiter.gherkin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.inject.Inject;

public class GherkinHttpCodeGeneratorPlugin implements Plugin<Project> {
    @Inject
    public GherkinHttpCodeGeneratorPlugin() {

    }

    @Override
    public void apply(Project project) {
        project.getTasks().register("genGherkinHttpAPI",
                GherkinHttpCodeGenerationTask.class, it -> {
                    it.setGroup("Code Generation");
                    it.setDescription("Generates Java bytecode representation of the OpenAPI.");
                });
    }
}
