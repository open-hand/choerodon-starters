package io.choerodon.liquibase;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.hzero.helper.generator.core.config.InstallerConfigProperties;
import org.hzero.helper.generator.core.domain.entity.Mapping;
import org.hzero.helper.generator.core.infra.util.XmlUtils;
import org.hzero.helper.generator.installer.service.ImportDataService;
import org.hzero.helper.generator.installer.service.UpdateDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.liquibase.enums.DbTypeEnum;
import io.choerodon.liquibase.utils.UnpackJar;


/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Value("${installer.jarPath:#{null}}")
    private String defaultJar;
    @Value("${installer.skipFile: }")
    private String skipFile;
    // 是否初始化依赖jar中的脚本
    @Value("${installer.jarPath.init:false}")
    private boolean defaultJarInit;
    @Value("${installer.fix-data-version:#{null}}")
    private String fixDataVersion;
    @Value("${installer.fix-data:false}")
    private Boolean fixData;

    @Value("${spring.datasource.dynamic.datasource.gen.driver-class-name}")
    private String driver;
    private List<Mapping> mappingList = XmlUtils.MAPPING_LIST;

    @Autowired
    private InstallerConfigProperties installerConfigProperties;

    private static final String TEMP_DIR_NAME = "choerodon/";
    private static final String FIX_DATA_VERSION_FORMAT = "%s/%s";

    private ImportDataService importDataService;
    private UnpackJar unpackJar;
    private UpdateDataService updateDataService;

    public StartupRunner(ImportDataService importDataService, UnpackJar unpackJar, UpdateDataService updateDataService) {
        this.importDataService = importDataService;
        this.unpackJar = unpackJar;
        this.updateDataService = updateDataService;
    }

    @Override
    public void run(String... args) {
        try {
            if (!StringUtils.isEmpty(defaultJar)) {
                // 解压jar包文件
                unpackJar.extra(defaultJar, TEMP_DIR_NAME, defaultJarInit, skipFile);
                // 执行groovy脚本
                List<String> groovyFileNames = getFileName(installerConfigProperties.getGroovyDir());
                if (!CollectionUtils.isEmpty(groovyFileNames)) {
                    XmlUtils.resolver(installerConfigProperties.getMappingFile());
                    Map<String, Mapping> mappingMap = new HashMap<>();
                    mappingList.forEach(m -> mappingMap.put(m.getFilename(), m));
                    List<String> groovyNames = groovyFileNames.stream().map(t -> mappingMap.get(t).getName()).collect(Collectors.toList());
                    if (!importDataService.selfGroovy(groovyNames, false, installerConfigProperties.getGroovyDir())) {
                        throw new Exception("初始化groovy脚本失败！");
                    }
                }
                // 执行init-data 数据
                List<String> initNames = getFileName(installerConfigProperties.getDataDir());
                // 如果存在插件，则初始化插件
                addPlugin(initNames, installerConfigProperties.getDataDir());
                if (!CollectionUtils.isEmpty(initNames)) {
                    XmlUtils.resolver(installerConfigProperties.getMappingFile());
                    String value = System.getProperties().getProperty("data.init", "true");
                    if (!Objects.isNull(value) && Boolean.TRUE.equals(Boolean.valueOf(value))) {
                        if (!importDataService.selfData(initNames, installerConfigProperties.getDataDir())) {
                            throw new Exception("初始化excel失败！");
                        }
                    }
                }
                // 执行修复数据
                if (fixData && !StringUtils.isEmpty(fixDataVersion)) {
                    XmlUtils.resolver(installerConfigProperties.getMappingFile());
                    String fixVersion = String.format(FIX_DATA_VERSION_FORMAT, fixDataVersion, getDatabaseName());
                    boolean result = updateDataService.dataUpdate(fixVersion);
                    if (!result) {
                        throw new Exception("数据修复完成,中间存在错误,请查看！");
                    }
                }
                logger.info("数据库初始化任务完成！");
            }
        } catch (Exception e) {
            logger.error("数据库初始化任务失败, message: {}, exception: ", e.getMessage(), e);
            System.exit(1);
        }
        System.exit(0);
    }

    private void addPlugin(List<String> initNames, String dataDir) {
        mappingList.forEach(mapping -> {
            List<Mapping> plugins = mapping.getPlugins();
            plugins.forEach(plugin -> {
                String fileName = dataDir + File.separator + mapping.getName() + File.separator + mapping.getFilename() + File.separator + plugin.getFilename();
                if (new File(fileName).exists()) {
                    initNames.add(plugin.getName());
                }
            });
        });

    }

    /**
     * 获取文件名
     *
     * @param path
     * @return
     * @throws IOException
     */
    private List<String> getFileName(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            logger.warn("File does not exist!");
            return null;
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                logger.info("The db directory is empty!");
                return null;
            }
            List<String> list = new ArrayList<>();
            for (File listFile : listFiles) {
                list.add(listFile.getName());
            }
            return list;
        }
    }

    /**
     * 获取文件名
     *
     * @param path
     * @return
     * @throws IOException
     */
    private List<String> recurveDriectory(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            logger.warn("File does not exist!");
            return null;
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                logger.info("The db directory is empty!");
                return null;
            }
            List<String> list = new ArrayList<>();
            for (File listFile : listFiles) {
                list.add(listFile.getName());
            }
            return list;
        }
    }

    /**
     * 获取数据库类型
     *
     * @return
     */
    private String getDatabaseName() {
        if (driver.contains(DbTypeEnum.MYSQL.type())) {
            return DbTypeEnum.MYSQL.type();
        } else if (driver.contains(DbTypeEnum.ORACLE.type())) {
            return DbTypeEnum.ORACLE.type();
        } else if (driver.contains(DbTypeEnum.SQLSERVER.type())) {
            return DbTypeEnum.SQLSERVER.type();
        } else if (driver.contains(DbTypeEnum.POSTGRES.type())) {
            return DbTypeEnum.POSTGRES.type();
        }
        throw new RuntimeException("error.get.db.type");
    }
}
