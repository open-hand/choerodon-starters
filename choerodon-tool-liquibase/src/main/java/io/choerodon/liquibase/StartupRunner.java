package io.choerodon.liquibase;

import io.choerodon.liquibase.enums.DbTypeEnum;
import io.choerodon.liquibase.utils.UnpackJar;
import org.hzero.helper.core.config.CoreConfigProperties;
import org.hzero.helper.core.domain.entity.Mapping;
import org.hzero.helper.core.infra.util.XmlUtils;
import org.hzero.helper.installer.service.ImportDataService;
import org.hzero.helper.installer.service.UpdateDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


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
    private List<Mapping> mappingList = XmlUtils.MAPPING_LIST;

    @Autowired
    private CoreConfigProperties installerConfigProperties;

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
            if (!ObjectUtils.isEmpty(defaultJar)) {
                CoreConfigProperties.Upgrade upgrade = installerConfigProperties.getUpgrade();
                // 解压jar包文件
                unpackJar.extra(defaultJar, TEMP_DIR_NAME, defaultJarInit, skipFile);
                // 服务启动时还没有把mapping文件解压出来，需要手动装载
                if (CollectionUtils.isEmpty(mappingList)) {
                    XmlUtils.resolver(installerConfigProperties.getData());
                }
                // 执行groovy脚本
                List<String> groovyFileNames = getFileName(upgrade.getGroovyDir());
                if (!CollectionUtils.isEmpty(groovyFileNames)) {
                    Map<String, Mapping> mappingMap = new HashMap<>();
                    mappingList.forEach(m -> mappingMap.put(m.getFilename(), m));
                    List<String> groovyNames = groovyFileNames.stream().map(t -> mappingMap.get(t).getName()).collect(Collectors.toList());
                    if (!importDataService.selfGroovy(groovyNames, false, upgrade.getGroovyDir())) {
                        throw new Exception("初始化groovy脚本失败！");
                    }
                }

                // 执行init-data 数据
                List<String> initNames = getFileName(upgrade.getDataDir());
                // 如果存在插件，则初始化插件
                addPlugin(initNames, upgrade.getDataDir());
                if (!CollectionUtils.isEmpty(initNames)) {
                    String value = System.getProperties().getProperty("data.init", "true");
                    if (!Objects.isNull(value) && Boolean.TRUE.equals(Boolean.valueOf(value))) {
                        if (!importDataService.selfData(initNames, upgrade.getDataDir())) {
                            throw new Exception("初始化excel失败！");
                        }
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
            if (!CollectionUtils.isEmpty(plugins)) {
                plugins.forEach(plugin -> {
                    String fileName = dataDir + File.separator + mapping.getName() + File.separator + mapping.getFilename() + File.separator + plugin.getFilename();
                    if (new File(fileName).exists()) {
                        initNames.add(plugin.getName());
                    }
                });
            }

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

}
