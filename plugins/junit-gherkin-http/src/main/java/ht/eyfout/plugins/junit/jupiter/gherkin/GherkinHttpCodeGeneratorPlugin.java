package ht.eyfout.plugins.junit.jupiter.gherkin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class GherkinHttpCodeGeneratorPlugin implements Plugin<Project> {
    private final String ROOT_DIR = "/generated/junit-gherkin/";

    @Inject
    public GherkinHttpCodeGeneratorPlugin() {

    }

    String captitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void apply(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        Provider<Directory> outputDir = project.getLayout().getBuildDirectory().dir(ROOT_DIR + mainSourceSet.getName() + "/classes");
        mainSourceSet.getOutput().dir(outputDir);
        TaskProvider<GherkinHttpCodeGenerationTask> task = project.getTasks().register("httpAPICodeGen",
                GherkinHttpCodeGenerationTask.class, it -> {
                    it.setGroup("Code Generation");
                    it.setDescription("Generates bytecode that conforms to junit-gherkin's HTTP APIs for an swagger document.");
                    it.setOutputDir(outputDir);
                    it.setSourceSet(mainSourceSet.getName());
                });
        project.getTasks().named(mainSourceSet.getCompileJavaTaskName()).configure(new Action<Task>() {
            @Override
            public void execute(@NotNull Task compileJavaTask) {
                compileJavaTask.dependsOn(task);
            }
        });

        SourceSet pluginSourceSet = sourceSets.create("httpAPICodeGen");
        pluginSourceSet.getResources().srcDir(project.getLayout().getBuildDirectory().dir(ROOT_DIR + mainSourceSet.getName() + "/java"));
        pluginSourceSet.getOutput().dir(outputDir);
    }
}
