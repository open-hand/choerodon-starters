package io.choerodon.liquibase;

import io.choerodon.core.exception.CommonException;
import io.choerodon.liquibase.addition.AdditionDataSource;
import io.choerodon.liquibase.addition.ProfileMap;
import io.choerodon.liquibase.excel.ExcelDataLoader;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * 入口方法，LiquibaseExecutor，启动加载
 *
 * @author dongfan117@gmail.com
 * @see org.springframework.boot.CommandLineRunner
 */
@Component
public class LiquibaseExecutor implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseExecutor.class);
    private static final String TEMP_DIR_NAME = "temp/";
    private static final String SUFFIX_XLSX = ".xlsx";
    private static final String SUFFIX_GROOVY = ".groovy";
    private static final String SUFFIX_SQL = ".sql";

    @Value("${data.dir:#{null}}")
    private String defaultDir;
    @Value("${data.jar:#{null}}")
    private String jar;
    @Value("${data.drop:false}")
    private boolean defaultDrop;

    @Value("${data.update.exclusion:#{null}}")
    private String updateExclusion;

    // 额外源
    @Value("${addition.datasource.names:#{null}}")
    private String additionDataSourceNameProfile;

    private DataSource defaultDataSource;
    private ProfileMap profileMap;

    public LiquibaseExecutor(@Qualifier(value = "dataSource") DataSource defaultDataSource,
                             @Qualifier(value = "profileMap") ProfileMap profileMap) {
        this.defaultDataSource = defaultDataSource;
        this.profileMap = profileMap;
    }


    @Override
    public void run(String... args) {
        try {
            if (additionDataSourceNameProfile != null) {
                additionDataSourceExec();
            } else {
                simpleExec(defaultDir, jar, defaultDataSource, defaultDrop);
            }
            logger.info("数据库初始化任务完成");
            System.exit(0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("数据库初始化任务失败");
            System.exit(1);
        }
    }

    /**
     * 载入jar时，递归获取所有文件夹.
     */
    private List<File> getDirRecursive(File file) {
        ArrayList<File> dirList = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                dirList.addAll(Arrays.asList(files));
                List<File> tmpList = dirList.stream()
                        .map(this::getDirRecursive)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                dirList.addAll(tmpList);
            }
        }
        return dirList;
    }

    /**
     * 用于获取jar中匹配参数设定的文件夹.
     */
    private String getDirInJar(List<File> fileList, String searchDir) {
        if (searchDir == null) {
            throw new IllegalArgumentException("必须指定要进行搜索的根目录");
        }
        String res = null;
        for (File file : fileList) {
            String normalPath = file.getPath().replace("\\", "/");
            int index = normalPath.indexOf(searchDir);
            if (index != -1 && index + searchDir.length() == normalPath.length()) {
                res = normalPath;
                break;
            }
        }
        if (res == null) {
            throw new CommonException(searchDir + " not exist.");
        }
        return res;
    }

    /**
     * 根据多数据源解析文件
     */
    private void additionDataSourceExec() throws IOException, CustomChangeException, SQLException, LiquibaseException {
        String[] additionDataSourceNames = additionDataSourceNameProfile.split(",");
        List<AdditionDataSource> additionDataSourceList = new ArrayList<>();
        for (String a : additionDataSourceNames) {
            String url = profileMap.getAdditionValue(a + ".url");
            String username = profileMap.getAdditionValue(a + ".username");
            String password = profileMap.getAdditionValue(a + ".password");
            String dir = profileMap.getAdditionValue(a + ".dir");
            boolean drop = Boolean.parseBoolean(profileMap.getAdditionValue(a + ".drop"));

            AdditionDataSource additionDataSource = new AdditionDataSource(url, username, password, dir, drop);
            additionDataSourceList.add(additionDataSource);
        }
        if (jar == null) {
            simpleExec(defaultDir, null, defaultDataSource, defaultDrop);
            for (AdditionDataSource addition : additionDataSourceList) {
                simpleExec(addition.getDir(), null, addition.getDataSource(), addition.isDrop());
            }
        } else {
            extra(jar, TEMP_DIR_NAME);
            List<File> fileList = getDirRecursive(new File(TEMP_DIR_NAME));
            String catalog = null;
            try (Connection connection = defaultDataSource.getConnection()) {
                catalog = connection.getCatalog();
            }
            logger.info("{} 初始化", catalog);
            if (!StringUtils.isEmpty(defaultDir)) {
                simpleExec(getDirInJar(fileList, defaultDir), null, defaultDataSource, defaultDrop);
            } else {
                simpleExec(TEMP_DIR_NAME, null, defaultDataSource, defaultDrop);
            }
            for (AdditionDataSource addition : additionDataSourceList) {
                logger.info("{} 初始化", catalog);
                if (!StringUtils.isEmpty(addition.getDir())) {
                    simpleExec(getDirInJar(fileList,
                            addition.getDir()),
                            null,
                            addition.getDataSource(),
                            addition.isDrop());
                } else {
                    simpleExec(TEMP_DIR_NAME, null, defaultDataSource, defaultDrop);
                }
            }
        }
    }

    private void simpleExec(String dir, String jar, DataSource dataSource, boolean drop)
            throws IOException, CustomChangeException, SQLException, LiquibaseException {
        if (dir != null) {
            load(dir, dataSource, drop);
        } else if (jar != null) {
            extra(jar, TEMP_DIR_NAME);
            load(TEMP_DIR_NAME, dataSource, defaultDrop);
        } else {
            load(".", dataSource, defaultDrop);
        }
    }

    private void extra(String jar, String dir) throws IOException {
        logger.info("Jar拆解");
        boolean isUrl = jar.startsWith("https://") || jar.startsWith("http://") || jar.startsWith("file://");
        try (InputStream inputStream = isUrl ? new URL(jar).openStream() : new FileInputStream(jar)) {
            JarEntry entry = null;
            File temp = new File(dir);
            FileUtils.deleteDirectory(temp);
            if (!temp.mkdir()) {
                throw new IOException("create dir fail.");
            }
            try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
                while ((entry = jarInputStream.getNextJarEntry()) != null) {
                    String name = entry.getName();
                    File file = new File(dir + name);
                    if (entry.isDirectory()) {
                        if (!file.mkdirs()) {
                            throw new IOException("create dir fail.");
                        }
                    } else if (name.endsWith(SUFFIX_GROOVY)
                            || name.endsWith(SUFFIX_XLSX)
                            || name.endsWith(SUFFIX_SQL)) {
                        try (FileOutputStream outputStream = new FileOutputStream(file)) {
                            StreamUtils.copy(jarInputStream, outputStream);
                        }
                    }
                }
            }
        }
        logger.info("Jar拆解完成");
    }

    private void load(String dir, DataSource dataSource, boolean drop)
            throws IOException, CustomChangeException, SQLException, LiquibaseException {
        Map<String, Set<String>> updateExclusionMap = processExclusion();
        ResourceAccessor accessor = new CusFileSystemResourceAccessor(dir);
        Set<String> fileNameSet = accessor.list(null, File.separator, true, false, true);
        List<String> fileNameList = new ArrayList<>();
        fileNameList.addAll(fileNameSet);
        Collections.sort(fileNameList);
        if (drop) {
            Liquibase liquibase = new Liquibase("drop", accessor, new JdbcConnection(dataSource.getConnection()));
            liquibase.dropAll();
        }
        //执行groovy脚本
        for (String file : fileNameList) {
            if (file.endsWith(SUFFIX_GROOVY)) {
                Liquibase liquibase = new Liquibase(file, accessor, new JdbcConnection(dataSource.getConnection()));
                liquibase.update(new Contexts());
            }
        }
        //初始化数据
        for (String file : fileNameList) {
            if (file.endsWith(SUFFIX_XLSX)) {
                ExcelDataLoader loader = new ExcelDataLoader();
                Set<InputStream> inputStream = accessor.getResourcesAsStream(file);
                loader.setUpdateExclusionMap(updateExclusionMap);
                loader.execute(inputStream.iterator().next(), dataSource);
            }
        }
    }

    /**
     *
     * exclusion参数的格式是table1.column1, table1.column2, table2.column, table3
     * 此方法把string转为maps
     * @return
     */
    private Map<String,Set<String>> processExclusion() {
        Map<String, Set<String>> map = new HashMap<>();
        if (updateExclusion != null) {
            String[] array = updateExclusion.split(",");
            for (String str : array) {
                if (str != null && str.contains(".")) {
                    String[] strArray =  str.split("\\.");
                    String tableName = strArray[0];
                    String columnName = strArray[1];
                    Set<String> columns = map.get(tableName);
                    if (columns == null) {
                        Set<String> set = new HashSet<>();
                        set.add(columnName);
                        map.put(tableName, set);
                    } else {
                        columns.add(columnName);
                    }
                } else {
                    //排除整张表的情况
                    String tableName = str;
                    if (map.get(tableName) == null) {
                        map.put(tableName, null);
                    }
                }
            }
        }
        return map;

    }
}
