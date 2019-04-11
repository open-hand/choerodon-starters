package io.choerodon.plugin.maven;

import java.io.*;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;

@Mojo(name = "extract", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ExtractMojo extends AbstractMojo {
    private static final String REACT_COPY_FOLDER = "/generate-react";
    static final String CHOERODON_FOLDER_IN_JAR = "CHOERODON-META";
    static final String FORWARD_SLASH = "/";
    private static final String BACK_SLASH = "\\";

    @Inject
    private MavenProject mavenProject;

    public void execute() {
        ArtifactFilter artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
        mavenProject.setArtifactFilter(artifactFilter);
        Set<Artifact> artifacts = mavenProject.getArtifacts();
        String baseSaveFolderPath = mavenProject.getBuild().getDirectory();
        if (!artifacts.isEmpty()) {
            for (Artifact artifact : artifacts) {
                File file = artifact.getFile();
                if (file != null) {
                    StringBuilder saveFolderPath = new StringBuilder();
                    String namespace = artifact.getArtifactId();
                    saveFolderPath.append(baseSaveFolderPath).append(REACT_COPY_FOLDER).append(FORWARD_SLASH).append(namespace);
                    try {
                        if (file.isDirectory()){
                            copyResourceFromDirectory(file.getAbsolutePath(), saveFolderPath.toString().replace(BACK_SLASH, FORWARD_SLASH));
                        } else {
                            copyResourceFromJar(file.getAbsolutePath(), saveFolderPath.toString().replace(BACK_SLASH, FORWARD_SLASH));
                        }
                    } catch (IOException e) {
                        getLog().error("copy react resource failed.", e);
                    }
                }
            }
        }
    }

    private void copyResourceFromDirectory(String target, String baseSaveFolderPath) throws IOException {
        File customFile = new File(target + FORWARD_SLASH + CHOERODON_FOLDER_IN_JAR);
        if (customFile.isDirectory()){
            getLog().info("Extract: " + target);
            FileUtils.copyDirectory(customFile, new File(baseSaveFolderPath));
        }
    }

    private void copyResourceFromJar(String jarPath, String baseSaveFolderPath) throws IOException {
        try(JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            if(jarFile.getEntry(CHOERODON_FOLDER_IN_JAR) == null){
                return;
            }
            getLog().info("Extract: " + jarFile.getName());
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                if (!jarEntry.isDirectory()) {
                    String resourcePath = jarEntry.getName();
                    //if the file is under react source folder
                    if (resourcePath.startsWith(CHOERODON_FOLDER_IN_JAR)) {
                        //copy react source file to common folder
                        copyResourceToCommonFolder(resourcePath.replace(CHOERODON_FOLDER_IN_JAR, ""),
                                baseSaveFolderPath, jarFile.getInputStream(jarEntry));
                    }
                }
            }
        }
    }

    private void copyResourceToCommonFolder(String resourcePath, String baseSaveFolderPath, InputStream is) throws IOException {
        if (is == null) {
            throw new FileNotFoundException("File " + resourcePath + " was not found inside JAR.");
        }
        if (!resourcePath.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }
        if (resourcePath.endsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (cat not end with '/').");
        }
        int index = resourcePath.lastIndexOf('/');
        String filename = resourcePath.substring(index + 1);
        //saveFolderPath = baseSaveFolderPath + resourceOriginPath
        String folderPath = baseSaveFolderPath + resourcePath.substring(0, index + 1);
        // If the folder does not exist yet, it will be created. If the folder
        // exists already, it will be ignored
        File dir = new File(folderPath);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        // If the file does not exist yet, it will be created. If the file
        // exists already, it will be ignored
        File file = new File(folderPath + filename);
        if (!file.exists() && !file.createNewFile()) {
            getLog().error(String.format("create file :%s failed", filename));
            return;
        }
        try(OutputStream os = new FileOutputStream(file)) {
            IOUtils.copy(is, os);
        }
    }
}