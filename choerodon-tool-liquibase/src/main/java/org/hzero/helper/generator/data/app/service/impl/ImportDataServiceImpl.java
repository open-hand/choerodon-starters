package org.hzero.helper.generator.data.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import groovy.ChoerodonLiquibaseChangeLogParser;
import liquibase.Liquibase;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.parser.ext.GroovyLiquibaseChangeLogParser;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.helper.generator.core.config.CoreConfigProperties;
import org.hzero.helper.generator.core.config.InstallerConfigProperties;
import org.hzero.helper.generator.core.domain.entity.Config;
import org.hzero.helper.generator.core.domain.entity.Mapping;
import org.hzero.helper.generator.core.infra.liquibase.CusFileSystemResourceAccessor;
import org.hzero.helper.generator.core.infra.liquibase.LiquibaseExecutor;
import org.hzero.helper.generator.core.infra.liquibase.helper.LiquibaseHelper;
import org.hzero.helper.generator.core.infra.mapper.InitDataMapper;
import org.hzero.helper.generator.core.infra.util.XmlUtils;
import org.hzero.helper.generator.installer.constant.LiquibaseChangeTableEnum;
import org.hzero.helper.generator.installer.dto.DataSourceDTO;
import org.hzero.helper.generator.installer.dto.TableChangeDTO;
import org.hzero.helper.generator.installer.service.ImportDataService;
import org.hzero.helper.generator.installer.utils.CheckedServiceUtil;
import org.hzero.helper.generator.installer.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * @Description 导入数据实现
 * @Date 2019/12/16 15:04
 * @Author wanshun.zhang@hand-china.com
 */
@Service
@Primary
public class ImportDataServiceImpl implements ImportDataService {

    private static final String ORACLE = "oracle";
    private static final String MYSQL = "mysql";
    private static final String SQLSERVER = "sqlserver";
    private static final String POSTGRESQL = "postgresql";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataServiceImpl.class);
    @Autowired
    InitDataMapper initDataMapper;
    @Autowired
    InstallerConfigProperties configProperties;
    @Autowired
    private CoreConfigProperties properties;
    /**
     * 服务列表
     */
    private List<Mapping> mappingList = XmlUtils.MAPPING_LIST;
    private Map<String, Mapping> serviceMapping = XmlUtils.SERVICE_MAPPING;
    private Map<String, Config> schema_marge = XmlUtils.SCHEMA_MERGE;
    private LiquibaseExecutor liquibaseExecutor = new LiquibaseExecutor();

    @Value("${data.version}")
    private String version;
    @Value("${server.port}")
    private String port;
    @Value("${spring.datasource.dynamic.datasource.gen.url}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.gen.username}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.gen.password}")
    private String password;

    /**
     * 获取版本信息
     *
     * @return 版本
     */
    @Override
    public String getVersion() {
        if (StringUtils.isNotBlank(version)) {
            return version;
        }
        return null;
    }

    @Override
    public String getWebSocketUrl() {
        try {
            return "ws://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/websocket";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取服务列表
     *
     * @return 服务列表
     */
    @Override
    public List<Mapping> getDataServices() {
        String dataDir = configProperties.getDataDir();
        File file = new File(dataDir);
        if (!file.exists()) {
            LOGGER.error(dataDir + "<<<<<<< 不存在");
        }
        List<Mapping> mappings = null;
        if (file.isDirectory()) {
            List<String> fileNames = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
            // data最外层文件夹名称与mapping文件的服务名配置一致
            mappings = mappingList.stream().filter(mapping -> fileNames.contains(mapping.getName())).collect(Collectors.toList());
        }
        assert mappings != null;
        return getMappings(mappings);
    }

    /**
     * 获取服务列表
     *
     * @return 服务列表
     */
    @Override
    public List<Mapping> getGroovyServices() {
        String groovyDir = configProperties.getGroovyDir();
        File file = new File(groovyDir);
        if (!file.exists()) {
            LOGGER.error(groovyDir + "<<<<<<< 不存在");
        }
        List<Mapping> mappings = null;
        if (file.isDirectory()) {
            List<String> fileNames = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
            // groovy最外层文件夹名称与mapping文件的文件名配置一致
            mappings = mappingList.stream().filter(mapping -> fileNames.contains(mapping.getFilename())).collect(Collectors.toList());
        }
        assert mappings != null;
        return getMappings(mappings);
    }

    private List<Mapping> getMappings(List<Mapping> mappings) {
        List<String> checkedServices = CheckedServiceUtil.getCheckedServices();
        Config config = getConfig(url);
        Assert.notNull(config, ">>> The service-mapping config must not be null");
        // 为 mapping 设置是否可勾选的标识
        mappings.forEach(mapping -> {
            if (StringUtils.equals(config.getMerge(), "true")) {
                mapping.setSchema(StringUtils.defaultIfBlank(config.getTargetSchema(), mapping.getSchema()));
                if (StringUtils.equals(ORACLE, config.getName())) {
                    mapping.setSchema(StringUtils.defaultIfBlank(StringUtils.substringAfterLast(url, ":"), mapping.getSchema()));
                }
            }
            if (CheckedServiceUtil.isNeedChecked(mapping.getName())) {
                // 设置勾选标识
                mapping.setCheck("true");
            }
        });
        return mappings;
    }

    /**
     * 导入数据
     *
     * @param services 服务列表
     * @param enable
     * @return 状态
     */
    @Override
    public boolean importData(List<String> services, boolean enable) throws Exception {
        String dataDir = configProperties.getDataDir();
        // 设置是否更新全量数据
        liquibaseExecutor.setEnable(enable);
        liquibaseExecutor.setRecursive(configProperties.isRecursive());
        Map<String, Mapping> mapping = new HashMap<>();
        mappingList.forEach(m -> mapping.put(m.getName(), m));
        Map<String, String> mappingMap = new HashMap<>();
        // 得到文件名和数据库的map
        mappingList.forEach(m -> mappingMap.put(m.getFilename(), m.getSchema()));
        // 得到文件名和服务的mapping
        Map<String, Mapping> fileNameMapping = new HashMap<>();
        mappingList.forEach(m -> fileNameMapping.put(m.getFilename(), m));
        Config config = getConfig(url);
        assert config != null;
        for (String service : services) {
            String pluginName = null;
            if (StringUtils.contains(service, "/")) {
                pluginName = service;
            }
            service = StringUtils.substringBefore(service, "/");
            String rootPath = dataDir + File.separator + service;
            File file = new File(rootPath);
            if (file.isDirectory()) {
                List<String> filenameList = Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(f -> !f.isFile()).map(File::getName).collect(Collectors.toList());
                LOGGER.info("******************** start {} service ********************", service);
                for (String filename : filenameList) {
                    // 对应依赖服务
                    String relyService = fileNameMapping.get(filename).getName();
                    // 处理数据库
                    String schema = mappingMap.get(filename);
                    if (StringUtils.equals(config.getMerge(), "true")) {
                        if (StringUtils.equals(ORACLE, config.getName())) {
                            schema = (StringUtils.defaultIfBlank(StringUtils.substringAfterLast(url, ":"), mappingMap.get(filename)));
                        } else {
                            schema = StringUtils.defaultIfBlank(config.getTargetSchema(), mappingMap.get(filename));
                        }
                    }
                    if (pluginName != null) {
                        Mapping serviceMapping = mapping.get(service);
                        List<Mapping> plugins = serviceMapping.getPlugins();
                        for (Mapping m : plugins) {
                            if (StringUtils.equals(m.getName(), pluginName)) {
                                filename = filename + File.separator + m.getFilename();
                                break;
                            }
                        }
                    } else {
                        liquibaseExecutor.setSkipFile(filename);
                    }
                    String dir = rootPath + File.separator + filename;
                    if (new File(dir).isDirectory()) {
                        executor(dir, relyService, schema);
                    } else {
                        LOGGER.warn("目录不存在：" + dir);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean selfData(List<String> services, String dataDir) throws Exception {
        configProperties.setDataDir(dataDir);
        //this.dataDir = dataDir;
        // 全部更新
        importData(services, true);
        return true;
    }

    /**
     * 更新脚本
     *
     * @param serviceNames 服务名列表
     * @return 状态
     */
    @Override
    public boolean updateGroovy(List<String> serviceNames, boolean enableFilter) throws Exception {
        String groovyDir = configProperties.getGroovyDir();
        liquibaseExecutor.setRecursive(true);
        Map<String, Mapping> mappingMap = new HashMap<>();
        mappingList.forEach(m -> mappingMap.put(m.getName(), m));
        Map<String, String> configCheckMap = new HashMap<>();
        for (String serviceName : serviceNames) {
            // FIX20201104 处理创建数据库逻辑，兼容多数据源情况，多数据源时需在相应数据源下创建数据库
            Mapping mapping = mappingMap.get(serviceName);
            Config config;
            String dbUrl;
            String driverClassName = null;
            if (configProperties != null && configProperties.getDbSource() != null
                    && configProperties.getDbSource().getDatasources() != null && mapping.getEnv() != null) {
                Map<String, String> datasource = configProperties.getDbSource().getDatasources().get(mapping.getEnv());
                driverClassName = datasource.get("driver-class-name");
                dbUrl = datasource.get("url");
                config = getConfig(dbUrl);
                mapping.setUsername(datasource.get("username"));
                mapping.setPassword(datasource.get("password"));
            } else {
                dbUrl = url;
                config = getConfig(dbUrl);
            }
            assert config != null;
            if (StringUtils.equals(config.getMerge(), "true") && StringUtils.isNotBlank(config.getTargetSchema())) {
                // 确保同一个数据源下不会多次创建相同的数据库
                if (MapUtils.isEmpty(configCheckMap)) {
                    configCheckMap.put(config.getName(), config.getTargetSchema());
                    mapping.setSchema(config.getTargetSchema());
                    createDatabase(mapping, dbUrl, driverClassName);
                } else {
                    if (StringUtils.isBlank(configCheckMap.get(config.getName()))
                            || !config.getTargetSchema().equals(configCheckMap.get(config.getName()))) {
                        // 该数据源下未创建过该数据库
                        mapping.setSchema(config.getTargetSchema());
                        createDatabase(mapping, dbUrl, driverClassName);
                    }
                }
            } else {
                createDatabase(mapping, dbUrl, driverClassName);
            }
            // 处理数据库
            String schema = mapping.getSchema();
            if (StringUtils.equals(config.getMerge(), "true")) {
                if (StringUtils.equals(ORACLE, config.getName())) {
                    schema = (StringUtils.defaultIfBlank(StringUtils.substringAfterLast(dbUrl, ":"), mapping.getSchema()));
                } else {
                    schema = StringUtils.defaultIfBlank(config.getTargetSchema(), mapping.getSchema());
                }
            }
            String dir = groovyDir + File.separator + mapping.getFilename();
            executor(dir, mapping.getName(), schema, enableFilter);
        }
        return true;
    }

    @Override
    public boolean selfGroovy(List<String> services, boolean enableFilter, String groovyDir) throws Exception {
        configProperties.setGroovyDir(groovyDir);
        //this.groovyDir = groovyDir;
        updateGroovy(services, enableFilter);
        return true;
    }

    private void executor(String dir, String service, String schema) throws Exception {
        executor(dir, service, schema, false);
    }

    private void executor(String dir, String service, String schema, boolean enableFilter) throws Exception {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>> start : service={}, schema={} <<<<<<<<<<<<<<<<<<<<<<<<<<<<<", service, schema);
        Mapping mapping = serviceMapping.get(service);
        if (mapping == null) {
            LOGGER.error("通过{}服务名无法获取mapping", service);
            return;
        }
        DataSourceDTO dataSourceDTO = reconstructDsInfo(mapping);
        liquibaseExecutor.setDsUrl(dataSourceDTO.getDbUrl());
        liquibaseExecutor.setDsUserName(dataSourceDTO.getUsername());
        liquibaseExecutor.setDsPassword(dataSourceDTO.getPassword());
        liquibaseExecutor.setDefaultDir(dir);
        liquibaseExecutor.execute(enableFilter);
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> end : service={}, schema={} <<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n\n\n", service, schema);
    }

    /**
     * 创建schema
     *
     * @param mapping         映射信息
     * @param dbUrl           数据源URL，可为空，若该URL存在值则以该值为准
     * @param driverClassName 驱动类
     */
    private void createDatabase(Mapping mapping, String dbUrl, String driverClassName) {
        String schema = mapping.getSchema();
        boolean dynamicFlag = false;
        if (!dbUrl.equals(url)) {
            dynamicFlag = true;
        }
        Config config = getConfig(dbUrl);
        assert config != null;
        if (StringUtils.equals(ORACLE, config.getName()) && StringUtils.equals(config.getMerge(), "true")) {
            return;
        }
        try {
            LOGGER.info(">>>>>>>>>>> begin create schema : " + schema);
            if (dynamicFlag) {
                this.dynamicCreateDatabase(config.getName(), dbUrl, mapping.getUsername(), mapping.getPassword(), schema, driverClassName);
            } else {
                if (StringUtils.equals(MYSQL, config.getName())) {
                    initDataMapper.createDatabaseMysql(schema);
                } else if (StringUtils.equals(POSTGRESQL, config.getName())) {
                    initDataMapper.createSchema(schema);
                } else if (StringUtils.equals(SQLSERVER, config.getName())) {
                    initDataMapper.createDatabaseSqlServer(schema);
                } else {
                    initDataMapper.createDatabase(schema);
                }
            }
            LOGGER.info(schema + "<<<<<<<<<<< The automatic schema creation was successful");
        } catch (Throwable t) {
            if (t.getMessage().contains("exists")) {
                LOGGER.info("<<<<<<<<<<< The " + schema + " schema already exists");
            } else {
                LOGGER.info(t.getMessage());
            }
        }
    }

    /**
     * 获取config
     *
     * @param url 数据库url
     * @return config
     */
    private Config getConfig(String url) {
        if (StringUtils.contains(url, MYSQL)) {
            return schema_marge.get(MYSQL);
        } else if (StringUtils.contains(url, SQLSERVER)) {
            return schema_marge.get(SQLSERVER);
        } else if (StringUtils.contains(url, ORACLE)) {
            return schema_marge.get(ORACLE);
        } else if (StringUtils.contains(url, POSTGRESQL)) {
            return schema_marge.get(POSTGRESQL);
        }
        return null;
    }

    @Override
    public void generateScript(List<String> services, HttpServletResponse response) throws IOException {
        String groovyDir = configProperties.getGroovyDir();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=script.zip");

        ZipOutputStream zos = null;
        try {

            //1. 根据groovyDir，找到要比较的表名list
            Map<String, List<String>> tableMap = getTableMap(services, groovyDir);

            //2. 根据服务列表，找到对应的changelog
            Map<String, List<List<ChangeSet>>> currentChangeSetMap = getCurrentChangeSetMap(tableMap, services);

            //3. 根据groovyDir，找到新的changelog
            Map<String, List<List<ChangeSet>>> newChangeSetMap = getNewChangeSetMap(tableMap, services, groovyDir);

            //4. 对比两个changelog，并生成脚本（zip文件中包含多个服务名(以及多个服务合并)的文件夹，每个文件夹包含lock.sql、unlock.sql、readme.txt三个文件）
            zos = compareAndWriteOutputStream(tableMap, currentChangeSetMap, newChangeSetMap, response);

        } catch (IOException | LiquibaseException | SQLException e) {
            throw new IOException("generate script error: " + e.getMessage(), e);
        } finally {
            flushAndClose(zos);
            flushAndClose(response);
        }

    }


    private ZipOutputStream compareAndWriteOutputStream(Map<String, List<String>> tableMap, Map<String, List<List<ChangeSet>>> currentChangeSetMap, Map<String, List<List<ChangeSet>>> newChangeSetMap, HttpServletResponse response) throws IOException {
        OutputStream os = response.getOutputStream();
        byte[] readme = buildReadme();
        StringBuilder allUpgrade = new StringBuilder();
        StringBuilder adminSeedData = new StringBuilder();
        for (String service : currentChangeSetMap.keySet()) {
            byte[][] contents = compareAndGenerateFileContent(service, tableMap.get(service), currentChangeSetMap.get(service), newChangeSetMap.get(service));
            writeZipOutputStream(service, os, new Pair<>("lock.sql", contents[0]), new Pair<>("execute.sql", contents[1]), new Pair<>("unlock.sql", contents[2]), new Pair<>("upgrade.sql", contents[3]));
            allUpgrade.append(new String(contents[3])).append("\n");
            adminSeedData.append(new String(contents[4])).append("\n");
            //help GC
            contents = null;
        }
        return writeZipOutputStream(null, os, new Pair<>("readme.txt", readme), new Pair<>("maintain-tables.properties", adminSeedData.toString().getBytes()), new Pair<>("upgradeAll.sql", allUpgrade.toString().getBytes()));
    }

    private byte[] buildReadme() {
        return ("使用在线运维功能时，首先需要在admin服务执行initAdmin.sql，执行完毕后，在线运维界面会出现一个新的运维配置，" +
                "此时可以开启在线运维，使得应用层的请求不再进入持久层，用户访问相关运维表时，会响应\"运维中\"的提示。\n" +
                "然后，执行锁表脚本(即lock.sql)，自动等待持久层的请求执行结束，并获取表锁。\n" +
                "然后，对表执行加字段、加索引等命令，结束后执行释放锁脚本(即unlock.sql)，\n" +
                "最后在应用层关闭在线运维。\n" +
                "\n" +
                "- maintain-tables.properties: 在使用在线运维功能时，将maintain-tables.properties导入到环境，无需用户手动维护。\n" +
                "====== ps. maintain-tables.properties是根据groovy与changelog对比生成的在线运维数据，可在在线运维界面导入。\n" +
                "\n" +
                "- lock.sql: 在线运维时，需要先执行lock.sql脚本来锁表，然后再对表进行升级。\n" +
                "- execute.sql: 在线运维时，执行的DDL操作。（通过groovy分析出需要升级的表，以及生成相应的DDL语句）\n" +
                "- unlock.sql: 在线运维时，在执行lock.sql、execute.sql脚本后，需要执行unlock.sql释放表锁。\n" +
                "- upgrade.sql: 在线运维时，会依次执行lock表、DDL表、unlock表操作。是lock.sql、execute.sql、unlock.sql的聚合脚本。\n" +
                "====== ps. 每个服务均有各自的lock.sql、execute.sql、unlock.sql脚本，以便服务在不同数据源时执行。\n" +
                "\n" +
                "- upgradeAll.sql: 在线运维时，执行all-upgrade.sql则会完成锁表、DDL、释放表锁的整体流程。\n" +
                "====== ps. all-upgrade.sql仅适用于所有数据源均在同一Database的情况。\n"
        ).getBytes();
    }

    private byte[][] compareAndGenerateFileContent(String service, List<String> tables, List<List<ChangeSet>> currentChangeSets, List<List<ChangeSet>> newChangeSets) {

        byte[][] contents = new byte[5][];
        StringBuilder lock = new StringBuilder();
        StringBuilder execute = new StringBuilder();
        StringBuilder unlock = new StringBuilder("unlock tables;\n");
        StringBuilder maintainTables = new StringBuilder();

        if (!CollectionUtils.isEmpty(currentChangeSets)) {
            appendMaintainTablesKey(maintainTables, service);
            for (int i = 0; i < currentChangeSets.size(); i++) {
                String table = tables.get(i);
                List<ChangeSet> ccs = currentChangeSets.get(i);
                List<ChangeSet> ncs = newChangeSets.get(i);
                String toExecute = diff(ccs, ncs);
                if (!StringUtils.isEmpty(toExecute)) {
                    lock.append("lock tables ").append(table).append(" ").append(ifRead(toExecute) ? "read" : "write").append(";").append(" -- for mysql\n");
                    execute.append(toExecute);
                    appendMaintainTablesValue(maintainTables, table);
                }
            }
        }

        String lockString = lock.append("\n").toString();
        String executeString = execute.append("\n").toString();
        String unlockString = unlock.append("\n").toString();
        String maintainTablesString = maintainTables.deleteCharAt(maintainTables.length() - 1).append("\n").toString();
        contents[0] = lockString.getBytes();
        contents[1] = executeString.getBytes();
        contents[2] = unlockString.getBytes();
        contents[3] = (lockString + executeString + unlock).getBytes();
        contents[4] = maintainTablesString.getBytes();
        return contents;
    }

    private void appendMaintainTablesKey(StringBuilder maintainTables, String service) {
        maintainTables
                .append(service)
                .append(".read-mode-tables=");
    }

    private void appendMaintainTablesValue(StringBuilder maintainTables, String table) {
        maintainTables
                .append(table).append(",");
    }

    /**
     * 比对changeSet来生成DDL语句
     *
     * @param ccs
     * @param ncs
     * @return
     */
    private String diff(List<ChangeSet> ccs, List<ChangeSet> ncs) {
        List<Change> changes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ccs)) {
            ChangeSet cs = ccs.get(ccs.size() - 1);
            boolean match = false;
            for (int i = 0; i < ncs.size(); i++) {
                ChangeSet changeSet = ncs.get(i);
                if (match) {
                    changes.addAll(changeSet.getChanges());
                }
                if (cs.getId().equals(changeSet.getId())) {
                    match = true;
                }
            }
        } else {
            for (int i = 0; i < ncs.size(); i++) {
                ChangeSet changeSet = ncs.get(i);
                changes.addAll(changeSet.getChanges());
            }
        }
        if (!CollectionUtils.isEmpty(changes)) {
            return buildExecute(changes);
        }
        return "";
    }

    private String buildExecute(List<Change> changes) {
        StringBuilder builder = new StringBuilder();
        for (Change change : changes) {
            // mysql
            Sql[] sqls = SqlGeneratorFactory.getInstance().generateSql(change, new MySQLDatabase());
            for (Sql sql : sqls) {
                builder.append(sql).append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 根据执行语句决定采用读模式还是写模式
     * 默认写模式
     *
     * @param toExecute
     * @return
     */
    private boolean ifRead(String toExecute) {
        return false;
    }

    @SafeVarargs
    private final ZipOutputStream writeZipOutputStream(String dirName, OutputStream os, Pair<String, byte[]>... files) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(os);
        if (files != null && files.length > 0) {
            String prefix = org.springframework.util.StringUtils.isEmpty(dirName) ? "" : dirName + "/";
            for (Pair<String, byte[]> pair : files) {
                if (pair.getValue() == null || pair.getValue().length == 0) {
                    continue;
                }
                ZipEntry entry = new ZipEntry(prefix + pair.getKey());
                zos.putNextEntry(entry);
                zos.write(pair.getValue());
                zos.closeEntry();
            }
            zos.flush();
        }
        return zos;
    }

    private Map<String, List<String>> getTableMap(List<String> services, String groovyDir) throws IOException {
        Map<String, List<String>> tableMap = new HashMap<>();
        for (String service : services) {
            tableMap.putIfAbsent(service, createTableMapByGroovy(service, groovyDir));
        }
        return tableMap;
    }

    private List<String> createTableMapByGroovy(String service, String groovyDir) throws IOException {
        Mapping mapping = getMappingByService(service);
        ResourceAccessor accessor = new CusFileSystemResourceAccessor(groovyDir + File.separator + mapping.getSchema());
        Set<String> fileNameSet = accessor.list(null, File.separator, true, false, true);
        return fileNameSet.stream().filter(fileName -> fileName.endsWith(".groovy"))
                .map(fileName -> fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    private Map<String, List<List<ChangeSet>>> getNewChangeSetMap(Map<String, List<String>> tableMap, List<String> services, String groovyDir) throws LiquibaseException, IOException, SQLException {
        Map<String, List<List<ChangeSet>>> changeSetMap = new HashMap<>();
        for (String service : services) {
            List<String> tables = tableMap.get(service);
            changeSetMap.putIfAbsent(service, getChangeSetByGroovy(tables, service, groovyDir));
        }
        return changeSetMap;
    }

    private List<List<ChangeSet>> getChangeSetByGroovy(List<String> tables, String service, String groovyDir) throws LiquibaseException, IOException, SQLException {
        if (CollectionUtils.isEmpty(tables)) {
            return Collections.emptyList();
        }
        List<Liquibase> liquibaseList = getLiquibaseByGroovy(service, groovyDir);
        List<ChangeSet> changeSets = new ArrayList<>();
        for (Liquibase liquibase : liquibaseList) {
            changeSets.addAll(liquibase.getDatabaseChangeLog().getChangeSets());
        }
        return groupByTable(tables, changeSets);
    }

    private List<List<ChangeSet>> groupByTable(List<String> tables, List<ChangeSet> changeSets) {
        List<List<ChangeSet>> result = new ArrayList<>();
        for (String table : tables) {
            List<ChangeSet> cs = new ArrayList<>();
            Iterator<ChangeSet> iterator = changeSets.iterator();
            while (iterator.hasNext()) {
                ChangeSet changeSet = iterator.next();
                if (changeSet.getId().endsWith(table)) {
                    cs.add(changeSet);
                    iterator.remove();
                }
            }
            result.add(cs);
        }
        return result;
    }

    /**
     * 服务:表:ChangeSet
     *
     * @param tableMap
     * @param services
     * @return
     * @throws LiquibaseException
     * @throws SQLException
     */
    private Map<String, List<List<ChangeSet>>> getCurrentChangeSetMap(Map<String, List<String>> tableMap, List<String> services) throws LiquibaseException, SQLException {
        Map<String, List<List<ChangeSet>>> changeSetMap = new HashMap<>();
        for (String service : services) {
            List<String> tables = tableMap.get(service);
            changeSetMap.putIfAbsent(service, getChangeSetByDb(tables, service));
        }
        return changeSetMap;
    }

    private List<List<ChangeSet>> getChangeSetByDb(List<String> tables, String service) throws SQLException {
        if (CollectionUtils.isEmpty(tables)) {
            return Collections.emptyList();
        }
        DataSource dataSource = getDataSourceByService(service);
        return queryDatasource(tables, dataSource);
    }

    /**
     * 按表分割ChangeSet
     *
     * @param tables
     * @param dataSource
     * @return
     */
    private List<List<ChangeSet>> queryDatasource(List<String> tables, DataSource dataSource) throws SQLException {
        PreparedStatement statement = dataSource.getConnection().prepareStatement("SELECT ID, AUTHOR, FILENAME FROM DATABASECHANGELOG");
        ResultSet resultSet = statement.executeQuery();
        List<ChangeSet> changeSets = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString("ID");
            String author = resultSet.getString("AUTHOR");
            String filename = resultSet.getString("FILENAME");
            ChangeSet changeSet = new ChangeSet(id, author, false, false, filename, null, null, false, null, null);
            changeSets.add(changeSet);
        }
        return groupByTable(tables, changeSets);
    }

    private List<Liquibase> getLiquibaseByGroovy(String service, String groovyDir) throws LiquibaseException, IOException, SQLException {
        List<Liquibase> liquibaseList = new ArrayList<>();
        Mapping mapping = getMappingByService(service);
        ResourceAccessor accessor = new CusFileSystemResourceAccessor(groovyDir + File.separator + mapping.getSchema());
        Set<String> fileNameSet = accessor.list(null, File.separator, true, false, true);
        JdbcConnection jdbcConnection = new JdbcConnection(getDataSourceByService(service).getConnection());
        prepareLoadGroovy();
        for (String fileName : fileNameSet) {
            if (fileName.endsWith(".groovy")) {
                Liquibase liquibase = new Liquibase(fileName, accessor, jdbcConnection);
                liquibaseList.add(liquibase);
            }
        }
        return liquibaseList;
    }

    private DataSource getDataSourceByService(String service) {
        Mapping mapping = getMappingByService(service);
        DataSourceDTO dataSourceDTO = reconstructDsInfo(mapping);
        return new DriverManagerDataSource(dataSourceDTO.getDbUrl(), dataSourceDTO.getUsername(), dataSourceDTO.getPassword());
    }

    private Mapping getMappingByService(String service) {
        for (Mapping mapping : mappingList) {
            if (mapping.getName().equals(service)) {
                return mapping;
            }
        }
        throw new IllegalArgumentException("service[" + service + "] mapping not found");
    }

    private void flushAndClose(HttpServletResponse response) throws IOException {
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    private void flushAndClose(ZipOutputStream zos) throws IOException {
        if (zos != null) {
            zos.flush();
            zos.close();
        }
    }

    @Override
    public DataSourceDTO reconstructDsInfo(Mapping mapping) {

        String schema = mapping.getSchema();
        String dbUrl = this.url;
        String username = this.username;
        String password = this.password;
        // 多数据源跨db隐藏配置
        if (configProperties != null && configProperties.getDbSource() != null &&
                configProperties.getDbSource().getDatasources() != null && mapping.getEnv() != null) {
            Map<String, String> datasource = configProperties.getDbSource().getDatasources().get(mapping.getEnv());
            if (datasource != null) {
                dbUrl = datasource.get("url");
                username = datasource.get("username");
                password = datasource.get("password");
            }
        }
        Config config = getConfig(dbUrl);
        assert config != null;
        if (StringUtils.equals(config.getName(), MYSQL)) {
            // todo 覆盖该类 唯一自定义逻辑
            dbUrl = dbUrl.replace("/?", "?");
            dbUrl = StringUtils.replace(dbUrl, "?", "/" + schema + "?");
        } else if (StringUtils.equals(config.getName(), SQLSERVER)) {
            dbUrl += "DatabaseName=" + schema;
        } else if (StringUtils.equals(config.getName(), ORACLE) && StringUtils.equals(config.getMerge(), "false")) {
            username = StringUtils.defaultIfBlank(mapping.getUsername(), schema);
            password = StringUtils.defaultIfBlank(mapping.getPassword(), schema);
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> username : {} | password : {}", username, password);
        } else if (StringUtils.equals(POSTGRESQL, config.getName())) {
            dbUrl = dbUrl + "?currentSchema=" + schema;
//            dbUrl = StringUtils.replace(url, "/postgres", "/" + schema);
        }
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setDbUrl(dbUrl);
        dataSourceDTO.setUsername(username);
        dataSourceDTO.setPassword(password);
        return dataSourceDTO;
    }

    @Override
    public List<TableChangeDTO> scanTableChanges(String dbName, String description) {
        String groovyDir = configProperties.getGroovyDir();
        Assert.isTrue(groovyDir != null, "请在配置文件中添加 groovy 脚本根目录路径！！！");
        List<TableChangeDTO> tableChangeDTOList = new ArrayList<>();
        // 添加配置文件中配置的备份表
        Map<String, List<String>> stashTables = XmlUtils.STASH_TABLES;
        if (MapUtils.isNotEmpty(stashTables)) {
            stashTables.forEach((schema, tables) -> {
                for (String table : tables) {
                    tableChangeDTOList.add(new TableChangeDTO().setDbName(schema).setTableName(table).setDescription("数据修复脚本会影响的表，请按需进行备份！"));
                }
            });
        }
        List<String> checkedServiceList = new ArrayList<>();
        mappingList.forEach(mapping -> {
            if (dbName != null && !mapping.getSchema().contains(dbName)) {
                return;
            }
            DataSource datasource = getDataSourceByService(mapping.getName());
            List<ChangeSet> changeSets = new ArrayList<>();
            try {
                List<Liquibase> liquibaseList = getLiquibaseByGroovy(mapping.getName(), groovyDir);
                for (Liquibase liquibase : liquibaseList) {
                    List<ChangeSet> queryChangeSets = liquibase.getDatabaseChangeLog().getChangeSets();
                    List<ChangeSet> filterChangeSets = queryChangeSets.parallelStream()
                            .filter(changeSet -> {
                                boolean flag = true;
                                if (StringUtils.isNotBlank(description)) {
                                    flag = changeSet.getDescription().contains(description);
                                }
                                return LiquibaseChangeTableEnum.checkCommandExists(changeSet.getDescription()) && flag;
                            })
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(filterChangeSets)) {
                        // 将符合条件的 changeSet 放进集合，用于后续处理
                        filterChangeSets(filterChangeSets, datasource, changeSets);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(">>>>>>>解析groovy脚本获取changeSet失败！！！,异常信息：{}<<<<<<<<", e.getMessage());
                return;
            }
            // 循环 changeSet ，构建返回结果
            for (ChangeSet changeSet : changeSets) {
                // 规范为changeSet脚本的logicalFilePath参数需配置为script/db/tableName.groovy, 如果不按照这种模式配置则不支持备份
                String tableName = StringUtils.substringBefore(StringUtils.substringAfter(changeSet.getFilePath(), properties.getUpgrade().getScriptPath()), properties.getUpgrade().getSuffix());
                if (StringUtils.isBlank(tableName)) {
                    continue;
                }
                TableChangeDTO tableChange = new TableChangeDTO()
                        .setChangeSetId(changeSet.getId())
                        .setDbName(mapping.getSchema())
                        .setAuthor(changeSet.getAuthor())
                        .setServiceName(mapping.getName())
                        .setTableName(tableName)
                        .setDescription(changeSet.getDescription());
                tableChangeDTOList.add(tableChange);
            }
            checkedServiceList.add(mapping.getName());
        });
        CheckedServiceUtil.setCheckedServices(checkedServiceList);
        return tableChangeDTOList;
    }

    @Override
    public String backupTables(List<TableChangeDTO> tableChanges) {
        List<String> failedTables = new ArrayList<>();
        if (CollectionUtils.isEmpty(tableChanges)) {
            return "不存在需要备份的表！";
        }
        // 构建Map对象。key为databaseName，value为tableName，databaseName用于从mapping中获取数据源
        Map<String, Set<String>> dbTableMap = tableChanges.parallelStream().distinct().collect(Collectors
                .groupingBy(TableChangeDTO::getDbName,
                        Collectors.mapping(TableChangeDTO::getTableName, Collectors.toSet())));
        for (Mapping mapping : mappingList) {
            Set<String> tables = dbTableMap.get(mapping.getSchema());
            if (!CollectionUtils.isEmpty(tables)) {
                // 下面的表备份所需数据源
                DataSource datasource = getDataSourceByService(mapping.getName());
                // 处理备份表
                processBackupTable(tables, datasource, failedTables);
            }
        }
        if (CollectionUtils.isEmpty(failedTables)) {
            return "表结构备份成功";
        } else {
            return StringUtils.join("部分表结构备份失败，备份失败的表为 ", failedTables);
        }
    }

    private void processBackupTable(Set<String> tables, DataSource datasource, List<String> failedTables) {
        String stashTablePrefix = properties.getUpgrade().getStashTablePrefix();
        Connection conn = null;
        Statement statement = null;
        for (String tableName : tables) {
            String sql = null;
            try {
                conn = datasource.getConnection();
                String databaseType = conn.getMetaData().getDatabaseProductName();
                switch (databaseType) {
                    case "MySQL":
                    case "Oracle":
                        sql = StringUtils.join("CREATE TABLE ", stashTablePrefix, tableName, " (SELECT * FROM ", tableName, " WHERE 1=1);");
                        break;
                    case "Microsoft SQL Server":
                    case "PostgreSQL":
                        sql = StringUtils.join("SELECT * INTO ", stashTablePrefix, tableName, " FROM ", tableName, " WHERE 1 = 1;");
                    default:
                        break;
                }
                statement = conn.createStatement();
                statement.executeUpdate(sql);
            } catch (Exception e) {
                LOGGER.error("表结构备份失败, 失败原因为： {}", e.getMessage());
                // 记录失败的表名称
                failedTables.add(tableName);
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        LOGGER.error("statement关闭失败，异常信息为： {}", e.getMessage());
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        LOGGER.error("connection关闭失败，异常信息为： {}", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 筛选DB中不存在的changeSet
     */
    private void filterChangeSets(List<ChangeSet> filterChangeSets, DataSource dataSource, List<ChangeSet> results) throws SQLException {
        Assert.isTrue(!CollectionUtils.isEmpty(filterChangeSets), "ChangeSet集合 不可为空！！！");
        Connection conn = null;
        PreparedStatement statement = null;
        for (ChangeSet changeSet : filterChangeSets) {
            try {
                conn = dataSource.getConnection();
                statement = conn
                        .prepareStatement("SELECT 1 FROM DATABASECHANGELOG WHERE ID = " + "\'" + changeSet.getId() + "\'");
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    // DB中不存在该记录，将其添加到changeSet集合中
                    results.add(changeSet);
                    LOGGER.info("changeSetId 为 {} 的记录在目标数据库中不存在，为变更记录。", changeSet.getId());
                } else {
                    LOGGER.info("changeSetId 为 {} 的记录已存在，为历史变更记录。", changeSet.getId());
                }
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        LOGGER.error("statement关闭失败，异常信息为： {}", e.getMessage());
                    }
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        LOGGER.error("connection关闭失败，异常信息为： {}", e.getMessage());
                    }
                }
            }
        }
    }

    private void prepareLoadGroovy() {
        List<ChangeLogParser> changeLogParsers =
                ChangeLogParserFactory
                        .getInstance()
                        .getParsers()
                        .stream()
                        .filter(parser -> parser instanceof GroovyLiquibaseChangeLogParser)
                        .collect(Collectors.toList());
        changeLogParsers.forEach(changeLogParser -> ChangeLogParserFactory.getInstance().unregister(changeLogParser));
        ChangeLogParserFactory.getInstance().register(new ChoerodonLiquibaseChangeLogParser(new LiquibaseHelper(url)));
    }

    /**
     * 动态创建数据库
     *
     * @param dbType          数据库类型
     * @param dbUrl           连接URL
     * @param username        用户名
     * @param password        密码
     * @param driverClassName 驱动类
     */
    private void dynamicCreateDatabase(String dbType, String dbUrl, String username, String password, String schema, String driverClassName) {
        Connection conn = null;
        Statement stmt = null;
        try {
            String sql;
            switch (dbType) {
                case MYSQL:
                    driverClassName = driverClassName == null ? "com.mysql.jdbc.Driver" : driverClassName;
                    sql = "create database " + schema + " CHARACTER SET utf8mb4 COLLATE utf8mb4_bin";
                    break;
                case SQLSERVER:
                    driverClassName = driverClassName == null ? "com.microsoft.sqlserver.jdbc.SQLServerDriver" : driverClassName;
                    sql = "create database " + schema + " COLLATE  Chinese_PRC_CI_AS ";
                    break;
                case POSTGRESQL:
                    driverClassName = driverClassName == null ? "org.postgresql.Driver" : driverClassName;
                    sql = "create schema " + schema;
                    break;
                case ORACLE:
                    driverClassName = driverClassName == null ? "oracle.jdbc.driver.OracleDriver" : driverClassName;
                    sql = "create database " + schema;
                    break;
                default:
                    // 暂不支持其它数据源
                    throw new RuntimeException("目前仅支持mysql、oracle、sqlserver、pgSql数据库！！！");
            }
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(dbUrl, username, password);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("数据库创建失败，异常信息为： {}", e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    LOGGER.error("statement关闭失败，异常信息为： {}", e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.error("connection关闭失败，异常信息为： {}", e.getMessage());
                }
            }
        }

    }

}
