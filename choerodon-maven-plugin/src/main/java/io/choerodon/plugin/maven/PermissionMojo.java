package io.choerodon.plugin.maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.annotation.PermissionProcessor;
import io.choerodon.annotation.entity.PermissionDescription;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static io.choerodon.plugin.maven.ExtractMojo.CHOERODON_FOLDER_IN_JAR;

@Mojo(name = "permission", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class PermissionMojo extends AbstractMojo {
    private static final String PERMISSION_FILE_NAME = "permission.json";
    @Inject
    private MavenProject mavenProject;

    @Parameter(property = "serviceBuild", defaultValue = "false")
    private boolean serviceBuild;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, PermissionDescription> report = new HashMap<>();

    @Override
    public void execute() throws MojoExecutionException {
        File target = new File(mavenProject.getBuild().getOutputDirectory());
        if (!target.exists() || !target.isDirectory()) {
            getLog().info("skip resolve classes not found. run mvn compile first.");
            return;
        }
        try {
            report = new HashMap<>();
            resolveProject();
            if (serviceBuild){
                resolveDependency();
            }
            File permissionFile = new File(target.getAbsolutePath() + File.separator + CHOERODON_FOLDER_IN_JAR + File.separator + PERMISSION_FILE_NAME);
            FileUtils.forceMkdirParent(permissionFile);
            objectMapper.writeValue(permissionFile, report);
            getLog().info("resolve permission data record: " + report.size());
        } catch (Exception e) {
            throw new MojoExecutionException("resolve exception", e);
        }
    }

    private void resolveDependency() throws IOException {
        ArtifactFilter artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
        mavenProject.setArtifactFilter(artifactFilter);
        for(Artifact artifact : mavenProject.getArtifacts()){
            File file = artifact.getFile();
            if (file != null) {
                if (file.isDirectory()) {
                    resolveFormDirectory(file.getAbsolutePath());
                } else {
                    resolveFormJar(file.getAbsolutePath());
                }
            }
        }
    }

    private void resolveFormJar(String target) throws IOException {
        try(JarFile jarFile = new JarFile(target)) {
            ZipEntry entry = jarFile.getEntry(CHOERODON_FOLDER_IN_JAR + "/" + PERMISSION_FILE_NAME);
            if (entry != null){
                getLog().info("Resolve: " + target);
                Map <String, PermissionDescription> descriptions = objectMapper.readValue(jarFile.getInputStream(entry), objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, PermissionDescription.class));
                report.putAll(descriptions);
            }
        }
    }

    private void resolveFormDirectory(String target) throws IOException {
        File permissionFile = new File(target + File.separator + CHOERODON_FOLDER_IN_JAR + File.separator + PERMISSION_FILE_NAME);
        if (permissionFile.isFile()){
            getLog().info("Resolve: " + target);
            Map <String, PermissionDescription> descriptions = objectMapper.readValue(permissionFile, objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, PermissionDescription.class));
            report.putAll(descriptions);
        }
    }

    private void resolveProject() throws IOException, URISyntaxException, DependencyResolutionRequiredException {
        List<String> classPaths = mavenProject.getCompileClasspathElements();
        URL[] urls = new URL[classPaths.size()];
        for (int i = 0; i < classPaths.size(); i++ ){
            urls[i] = new File(classPaths.get(i)).toURI().toURL();
        }
        try (URLClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader())) {
            Enumeration<URL> resources = classLoader.getResources("");
            List<File> dirs = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                URI uri = new URI(resource.toString());
                dirs.add(new File(uri.getPath()));
                getLog().info("permission resolve classpath : " + uri.getPath());
            }
            List<Class> classes = new ArrayList<>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, "", classLoader));
            }
            for (Class clazz : classes) {
                PermissionProcessor.resolveClass(clazz, report);
            }
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private List<Class> findClasses(File directory, String packageName, ClassLoader classLoader) {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        if (!packageName.isEmpty()){
            packageName += ".";
        }
        File[] files = directory.listFiles();
        if (files == null){
            return Collections.emptyList();
        }
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + file.getName(), classLoader));
            } else if (file.getName().endsWith(".class")) {
                String className =  packageName  + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(classLoader.loadClass(className));
                } catch (NoClassDefFoundError | ClassNotFoundException e) {
                    getLog().debug(String.format("skip class %s : %s", className, e.getMessage()), e);
                }
            }
        }
        return classes;
    }
}
