package io.choerodon.plugin.maven;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static io.choerodon.plugin.maven.ExtractMojo.CHOERODON_FOLDER_IN_JAR;

@Mojo(name = "convert", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ConvertMojo extends AbstractMojo {
    private static final int SKIP_ROW_NUMBER = 6;
    private static final int SKIP_CELL_NUMBER = 4;
    private static final String CONVERT_FILE_NAME = "script/front/micro-service-init-data.xlsx";
    private static final String MERGE_FILE_NAME = CHOERODON_FOLDER_IN_JAR + "/micro-service-init-data.json";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private MavenProject mavenProject;

    @Parameter(property = "serviceBuild", defaultValue = "false")
    private boolean serviceBuild;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File target = new File(mavenProject.getBuild().getOutputDirectory());
        if (!target.exists() || !target.isDirectory()) {
            getLog().info("skip convert classes not found. run mvn compile first.");
            return;
        }
        try {
            ObjectNode objectNode = convertFormDirectory(target.getAbsolutePath());
            if (objectNode == null){
                objectNode = JsonNodeFactory.instance.objectNode();
            }
            if (serviceBuild){
                mergeDependency(objectNode);
            }
            File outputFile = new File(target.getAbsolutePath() + File.separator + MERGE_FILE_NAME);
            FileUtils.forceMkdirParent(outputFile);
            FileUtils.writeStringToFile(outputFile, objectNode.toString(), Charset.defaultCharset());
        } catch (Exception e) {
            throw new MojoExecutionException("convert exception", e);
        }
    }

    private void mergeDependency(ObjectNode root) throws IOException {
        ArtifactFilter artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
        mavenProject.setArtifactFilter(artifactFilter);
        for(Artifact artifact : mavenProject.getArtifacts()){
            File file = artifact.getFile();
            if (file != null) {
                if (file.isDirectory()) {
                    mergeFormDirectory(file.getAbsolutePath(), root);
                } else {
                    mergeFormJar(file.getAbsolutePath(), root);
                }
            }
        }
    }

    private void mergeFormJar(String target, ObjectNode root) throws IOException {
        try(JarFile jarFile = new JarFile(target)) {
            ZipEntry entry = jarFile.getEntry(MERGE_FILE_NAME);
            if (entry != null){
                getLog().info("Merge: " + target);
                mergeFormInputStream(jarFile.getInputStream(entry), root);
            }
        }
    }

    private void mergeFormDirectory(String target, ObjectNode root) throws IOException {
        File convertFile = new File(target + File.separator + MERGE_FILE_NAME);
        if (convertFile.isFile()){
            getLog().info("Merge: " + target);
            mergeFormInputStream(new FileInputStream(convertFile), root);
        }
    }

    private ObjectNode convertFormDirectory(String target) throws IOException {
        File convertFile = new File(target + File.separator + CONVERT_FILE_NAME);
        if (convertFile.isFile()){
            getLog().info("Convert: " + target);
            return convertFormInputStream(new FileInputStream(convertFile));
        }
        return null;
    }

    private ObjectNode convertFormInputStream(InputStream inputStream) throws IOException {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        try(Workbook workbook = XSSFWorkbookFactory.create(inputStream)){
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            for( int i = 0; i < workbook.getNumberOfSheets(); i++){
                Sheet sheet = workbook.getSheetAt(i);
                result.set(sheet.getSheetName(), convertFormSheet(sheet));
            }
        }
        return result;
    }

    private ArrayNode convertFormSheet(Sheet sheet){
        Row headerRow = sheet.getRow(SKIP_ROW_NUMBER);
        ArrayNode sheetNode = JsonNodeFactory.instance.arrayNode();
        String[] columns = new String[headerRow.getLastCellNum() - SKIP_CELL_NUMBER + 1];
        for (int cellIndex = SKIP_CELL_NUMBER; cellIndex < headerRow.getLastCellNum(); cellIndex++){
            columns[cellIndex - SKIP_CELL_NUMBER] = headerRow.getCell(cellIndex).getStringCellValue();
        }
        for (int rowIndex = SKIP_ROW_NUMBER + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++){
            Row dataRow = sheet.getRow(rowIndex);
            if (dataRow == null){
                getLog().warn(String.format("row is null break: %s, %d", sheet.getSheetName(), rowIndex));
                break;
            }
            ObjectNode rowNode = JsonNodeFactory.instance.objectNode();
            for (int cellIndex = SKIP_CELL_NUMBER; cellIndex < headerRow.getLastCellNum(); cellIndex++){
                String columnName = columns[cellIndex - SKIP_CELL_NUMBER];
                Cell cell = dataRow.getCell(cellIndex);
                if (cell == null){
                    rowNode.putNull(columnName);
                    continue;
                }
                if (cell.getCellType().equals(CellType.NUMERIC)){
                    rowNode.put(columnName, cell.getNumericCellValue());
                } else {
                    rowNode.put(columnName, cell.getStringCellValue());
                }
            }
            sheetNode.add(rowNode);
        }
        return sheetNode;
    }

    private void mergeFormInputStream(InputStream inputStream, ObjectNode root) throws IOException {
        JsonNode inputRoot = objectMapper.readTree(inputStream);
        Iterator<String> tableNames = inputRoot.fieldNames();
        while (tableNames.hasNext()){
            String tableName = tableNames.next();
            JsonNode oldNode = root.get(tableName);
            if (oldNode == null){
                root.set(tableName, inputRoot.get(tableName));
            } else {
                root.set(tableName, mergeTableJsonObject(oldNode, inputRoot.get(tableName)));
            }
        }
    }

    private ArrayNode mergeTableJsonObject(JsonNode oldNode, JsonNode newNode){
        ArrayNode tableNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < oldNode.size(); i++){
            tableNode.add(oldNode.get(i));
        }
        for (int i = 0; i < newNode.size(); i++){
            tableNode.add(newNode.get(i));
        }
        return tableNode;
    }

}
