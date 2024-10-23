package ht.eyfout.plugins.openapi.http.api;

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
import java.util.Map;

public class HttpCodeGeneratorPlugin implements Plugin<Project> {
    private final String ROOT_DIR = "/generated/junit-gherkin/";

    @Inject
    public HttpCodeGeneratorPlugin() {

    }

    String captitalize(String str) {
        if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;

    }

    @Override
    public void apply(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        sourceSets.forEach(srcSet -> {
            String sourceSetName = srcSet.getName();
            String taskName = "openapiHttpAPI";
            if (!sourceSetName.equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                taskName = taskName + captitalize(sourceSetName);
            }
            Provider<Directory> outputDir = project.getLayout().getBuildDirectory().dir("generated/junit-gherkin/" + sourceSetName + "/classes");
            TaskProvider<HttpCodeGenerationTask> myTask = project.getTasks().register(taskName,
                    HttpCodeGenerationTask.class, it -> {
                        it.setGroup("Code Generation");
                        it.setDescription("Generates bytecode that conforms to junit-gherkin's HTTP APIs representation of an endpoint for a given swagger document.");
                        it.setSpecsDir("src/" + sourceSetName + "/resources/junit-gherkin");
                        it.setOutputDir(outputDir);
                    });

            srcSet.getOutput().dir(Map.of("builtBy", myTask.getName()), outputDir);

            project.getTasks().named(srcSet.getCompileJavaTaskName()).configure(new Action<>() {
                @Override
                public void execute(@NotNull Task compileJavaTask) {
                    compileJavaTask.dependsOn(myTask);
                }
            });
        });
    }
}
