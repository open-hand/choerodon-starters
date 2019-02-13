package io.choerodon.liquibase;

import io.choerodon.liquibase.addition.AdditionDataSource;
import io.choerodon.liquibase.addition.ProfileMap;
import io.choerodon.liquibase.excel.ExcelDataLoader;
import io.choerodon.liquibase.helper.LiquibaseHelper;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.parser.ext.GroovyLiquibaseChangeLogParser;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class LiquibaseExecutor {
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


    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Value("${spring.datasource.username}")
    private String dsUserName;

    @Value("${spring.datasource.password}")
    private String dsPassword;

    private DataSource defaultDataSource;
    private ProfileMap profileMap;

    public LiquibaseExecutor(DataSource defaultDataSource,
                             ProfileMap profileMap) {
        this.defaultDataSource = defaultDataSource;
        this.profileMap = profileMap;
    }


    public boolean execute(String... args) {

        boolean successful = false;
        try {
            runToDb(prepareDataSources());
            logger.info("数据库初始化任务完成");
            successful = true;
        } catch (Exception e) {
            logger.error("数据库初始化任务失败, message: {}, exception: {}", e.getMessage(), e);
        }
        return successful;

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
            throw new io.choerodon.liquibase.exception.LiquibaseException(searchDir + " not exist.");
        }
        return res;
    }


    private List<AdditionDataSource> prepareDataSources() {
        List<AdditionDataSource> additionDataSources = new ArrayList<>();
        AdditionDataSource additionDataSource =
                new AdditionDataSource(this.dsUrl, this.dsUserName, this.dsPassword, this.defaultDir, this.defaultDrop, this.defaultDataSource);
        additionDataSources.add(additionDataSource);
        if (additionDataSourceNameProfile != null) {
            String[] additionDataSourceNames = additionDataSourceNameProfile.split(",");
            for (String dataSourceName : additionDataSourceNames) {
                String url = profileMap.getAdditionValue(dataSourceName + ".url");
                String username = profileMap.getAdditionValue(dataSourceName + ".username");
                String password = profileMap.getAdditionValue(dataSourceName + ".password");
                String dir = profileMap.getAdditionValue(dataSourceName + ".dir");
                boolean drop = Boolean.parseBoolean(profileMap.getAdditionValue(dataSourceName + ".drop"));

                AdditionDataSource ads = new AdditionDataSource(url, username, password, dir, drop);
                additionDataSources.add(ads);
            }
        }
        return additionDataSources;
    }

    /**
     * 根据多数据源解析文件
     */
    private void runToDb(List<AdditionDataSource> additionDataSourceList) throws IOException, CustomChangeException, SQLException, LiquibaseException {
        if (jar == null) {
            for (AdditionDataSource addition : additionDataSourceList) {
                String dir = (addition.getDir() == null ? "." : addition.getDir());
                load(dir, addition);
            }
        } else {
            extra(jar, TEMP_DIR_NAME);
            List<File> fileList = getDirRecursive(new File(TEMP_DIR_NAME));
            String catalog = null;
            try (Connection connection = defaultDataSource.getConnection()) {
                catalog = connection.getCatalog();
            }
            logger.info("{} 初始化", catalog);
            for (AdditionDataSource addition : additionDataSourceList) {
                logger.info("{} 初始化", catalog);
                if (!StringUtils.isEmpty(addition.getDir())) {
                    String dir = getDirInJar(fileList, addition.getDir());
                    load(dir, addition);
                } else {
                    load(TEMP_DIR_NAME, addition);
                }
            }
        }
    }

    private void prepareGroovyParser(LiquibaseHelper liquibaseHelper) {
        List<ChangeLogParser> changeLogParsers =
                ChangeLogParserFactory
                        .getInstance()
                        .getParsers()
                        .stream()
                        .filter(parser -> (parser instanceof GroovyLiquibaseChangeLogParser) || (parser instanceof ChoerodonLiquibaseChangeLogParser))
                        .collect(Collectors.toList());
        changeLogParsers.forEach(changeLogParser -> ChangeLogParserFactory.getInstance().unregister(changeLogParser));
        ChangeLogParserFactory.getInstance().register(new ChoerodonLiquibaseChangeLogParser(liquibaseHelper));
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

    private void load(String dir, AdditionDataSource additionDataSource)
            throws IOException, CustomChangeException, SQLException, LiquibaseException {
        prepareGroovyParser(additionDataSource.getLiquibaseHelper());
        Map<String, Set<String>> updateExclusionMap = processExclusion();
        ResourceAccessor accessor = new CusFileSystemResourceAccessor(dir);
        Set<String> fileNameSet = accessor.list(null, File.separator, true, false, true);
        List<String> nameList = new ArrayList<>(fileNameSet);
        Collections.sort(nameList);

        JdbcConnection jdbcConnection = new JdbcConnection(additionDataSource.getDataSource().getConnection());
        if (additionDataSource.isDrop()) {
            Liquibase liquibase = new Liquibase("drop", accessor, jdbcConnection);
            liquibase.dropAll();
        }
        Liquibase liquibase = new Liquibase("clearCheckSums", accessor, jdbcConnection);
        liquibase.clearCheckSums();

        //执行groovy脚本
        for (String file : nameList) {
            if (file.endsWith(SUFFIX_GROOVY)) {
                liquibase = new Liquibase(file, accessor, jdbcConnection);
                liquibase.update(new Contexts());
            }
        }

        //初始化数据
        for (String file : nameList) {
            if (file.endsWith(SUFFIX_XLSX)) {
                ExcelDataLoader loader = new ExcelDataLoader();
                Set<InputStream> inputStream = accessor.getResourcesAsStream(file);
                loader.setUpdateExclusionMap(updateExclusionMap);
                logger.info("begin to process excel : {}", file);
                loader.execute(inputStream.iterator().next(), additionDataSource);
            }
        }
    }

    /**
     * exclusion参数的格式是table1.column1, table1.column2, table2.column, table3
     * 此方法把string转为maps
     *
     * @return
     */
    private Map<String, Set<String>> processExclusion() {
        Map<String, Set<String>> map = new HashMap<>();
        if (updateExclusion != null) {
            String[] array = updateExclusion.split(",");
            for (String str : array) {
                if (str != null && str.contains(".")) {
                    String[] strArray = str.split("\\.");
                    String tableName = (strArray[0] == null ? null : strArray[0].toLowerCase());
                    String columnName = (strArray[1] == null ? null : strArray[1].toLowerCase());
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
                    if (map.get(str) == null) {
                        map.put(str, null);
                    }
                }
            }
        }
        return map;

    }
}
