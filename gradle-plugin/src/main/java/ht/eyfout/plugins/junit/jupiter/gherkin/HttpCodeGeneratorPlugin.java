package ht.eyfout.plugins.junit.jupiter.gherkin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
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
            String taskName = "junitGherkinHttpCodeGen";
            if (!sourceSetName.equals(SourceSet.MAIN_SOURCE_SET_NAME)) {
                taskName = taskName + captitalize(sourceSetName);
            }
            Provider<Directory> outputDir = project.getLayout().getBuildDirectory().dir("generated/junit-gherkin/" + srcSet.getName() + "/classes");
            TaskProvider<HttpCodeGenerationTask> myTask = project.getTasks().register(taskName,
                    HttpCodeGenerationTask.class, it -> {
                        it.setGroup("Code Generation");
                        it.setDescription("Generates bytecode that conforms to junit-gherkin's HTTP APIs representation of an endpoint for a given swagger document.");
                        it.setSpecsDir("src/" + sourceSetName + "/resources/junit-gherkin");
                        it.setOutputDir(outputDir);
                    });

            project.getTasks().named(srcSet.getCompileJavaTaskName()).configure(new Action<>() {
                @Override
                public void execute(@NotNull Task compileJavaTask) {
                    srcSet.getOutput().dir(Map.of("builtBy", myTask.getName()), myTask);
                    compileJavaTask.dependsOn(myTask);
                }
            });
        });
    }

    void configure(Project project, String name) {
        String sourceSetName = name;
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet newSrcSet = sourceSets.create("junitGherkin" + captitalize(sourceSetName));
        DirectoryProperty buildDirectory = project.getLayout().getBuildDirectory();
//        ConfigurableFileCollection byteCodes = project.files(new File(buildDirectory.getAsFile().get(), ROOT_DIR + sourceSetName + "/classes"));
//        newSrcSet.getOutput().dir(byteCodes);
//        newSrcSet.getOutput().getClassesDirs().plus(byteCodes);
        Provider<Directory> outputDir = buildDirectory.dir(ROOT_DIR + sourceSetName + "/classes");
//        newSrcSet.getAllSource().srcDir(buildDirectory.dir(ROOT_DIR + sourceSetName + "/java"));
        TaskProvider<HttpCodeGenerationTask> task = project.getTasks().register("httpAPICodeGen" + sourceSetName,
                HttpCodeGenerationTask.class, it -> {
                    it.setGroup("Code Generation");
                    it.setDescription("Generates bytecode that conforms to junit-gherkin's HTTP APIs representation of an endpoint for a given swagger document.");
//                    it.setSourceSet(sourceSetName);
                    it.setOutputDir(outputDir);
                });


        SourceSet srcSet = sourceSets.getByName(name);
        srcSet.getOutput().dir(sourceSets.getByName(name).getOutput().plus(newSrcSet.getOutput()));
        project.getTasks().named(srcSet.getCompileJavaTaskName()).configure(new Action<>() {
            @Override
            public void execute(@NotNull Task compileJavaTask) {
                compileJavaTask.dependsOn(task);
            }
        });
    }
}
