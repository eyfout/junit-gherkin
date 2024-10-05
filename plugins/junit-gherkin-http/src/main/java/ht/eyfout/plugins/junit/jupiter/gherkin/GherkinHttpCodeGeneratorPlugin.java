package ht.eyfout.plugins.junit.jupiter.gherkin;

import org.gradle.api.*;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public class GherkinHttpCodeGeneratorPlugin implements Plugin<Project> {
    @Inject
    public GherkinHttpCodeGeneratorPlugin() {

    }

    String captitalize(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    @Override
    public void apply(Project project) {
        SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get("sourceSets");
        sourceSets.all(sourceSet -> {
            Provider<Directory> outputDir = project.getLayout().getBuildDirectory().dir("generated/junit-gherkin/" + sourceSet.getName() + "/classes");
            TaskProvider<GherkinHttpCodeGenerationTask> task = project.getTasks().register("gherkinHttpAPICodeGen" + captitalize(sourceSet.getName()),
                    GherkinHttpCodeGenerationTask.class, it -> {
                        it.setGroup("Code Generation");
                        it.setDescription("Generates Java bytecode representation of the OpenAPI.");
                        it.setOutputDir(outputDir);
                        it.setSourceSet(sourceSet.getName());
//                        it.setSpecsDir(sourceSet.getResources().srcDir(""));
                    });
            project.getTasks().named(sourceSet.getCompileJavaTaskName()).configure(new Action<Task>() {
                @Override
                public void execute(Task compileJavaTask ) {
                    compileJavaTask.dependsOn(task);
                }
            });
        });
    }
}
