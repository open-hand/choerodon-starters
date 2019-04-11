package io.choerodon.plugin.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@Mojo(name = "copy", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class CopyMojo extends AbstractMojo {

    @Inject
    private MavenProject project;

    public void execute() {
        String basePath = project.getBasedir().getAbsolutePath();
        String outputPath = project.getBuild().getOutputDirectory();
        try {
            File packageJson = new File(basePath + "/package.json");
            if(packageJson.exists()){
                File outputDirectory = new File(outputPath + ExtractMojo.FORWARD_SLASH + ExtractMojo.CHOERODON_FOLDER_IN_JAR);
                if(!outputDirectory.exists()){
                    FileUtils.forceMkdir(outputDirectory);
                }
                getLog().info("Copy File: " + packageJson.getName());
                FileUtils.copyFileToDirectory(packageJson, outputDirectory);
            }
            File uiDir = new File(basePath + "/react");
            if(uiDir.exists() && uiDir.isDirectory()){
                File outputDirectory = new File(outputPath + ExtractMojo.FORWARD_SLASH + ExtractMojo.CHOERODON_FOLDER_IN_JAR + "/react");
                if(!outputDirectory.exists()){
                    FileUtils.forceMkdir(outputDirectory);
                }
                getLog().info("Copy Directory: " + uiDir.getName());
                FileUtils.copyDirectory(uiDir, outputDirectory);
            }
        } catch (IOException e) {
            getLog().error("copy package.json failed.", e);
        }
    }
}
