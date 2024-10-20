package ht.eyfout.plugins.openapi.http.api;

import ht.eyfout.openapi.http.api.GherkinHttpAPIGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

abstract class HttpCodeGenerationTask extends DefaultTask {

    @Inject
    public HttpCodeGenerationTask() {
    }

    @Input
    private String specsDir = "resources/junit-gherkin";
    @OutputDirectory
    Provider<Directory> outputDir = getProject().getLayout().getBuildDirectory().dir("generated/junit-gherkin");

    private String sourceSet;

    @TaskAction
    void action() {
        Directory specsDir = getProject().getLayout().getProjectDirectory().dir(getSpecsDir());
        if (specsDir.getAsFile().exists()) {
            specsDir.getAsFileTree().forEach(openAPI -> {
                String namespace = openAPI.getName();
                int fileExtension = namespace.indexOf('.');
                if (fileExtension > 0) {
                    namespace = namespace.substring(0, fileExtension);
                }
                GherkinHttpAPIGenerator.codeGen(openAPI.getAbsolutePath(), namespace)
                        .generate(getClass().getClassLoader())
                        .forEach(it -> {
                            int index = it.first().lastIndexOf('/');
                            File dir = new File(outputDir.get().getAsFile(), it.first().substring(0, index));
                            dir.mkdirs();
                            File fs = new File(dir, it.first().substring(index + 1) + ".class");
                            try (FileOutputStream os = new FileOutputStream(fs)) {
                                fs.createNewFile();
                                os.write(it.second());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            });
        }
    }

    public String getSpecsDir() {
        return specsDir;
    }

    public void setSpecsDir(String specsDir) {
        this.specsDir = specsDir;
    }

    void setOutputDir(Provider<Directory> outputDir) {
        this.outputDir = outputDir;
    }

    Provider<Directory> getOutputDir() {
        return outputDir;
    }
}
