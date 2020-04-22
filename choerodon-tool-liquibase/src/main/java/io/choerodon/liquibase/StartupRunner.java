package io.choerodon.liquibase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hzero.installer.service.ImportDataService;
import org.hzero.installer.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.liquibase.utils.UnpackJar;


/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
@Component
public class StartupRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Value("${installer.jarPath:#{null}}")
    private String defaultJar;
    // 是否初始化依赖jar中的脚本
    @Value("${installer.jarPath.init:false}")
    private boolean defaultJarInit;

    private static final String TEMP_DIR_NAME = "temp/";
    private static final String PREFIX_SCRIPT_DB = "script/db/";
    private static final String GROOVY_PATH = "groovy";
    private static final String MAPPING_PATH = "service-mapping.xml";
    private static final String INIT_PATH = "init-data";

    private ImportDataService importDataService;
    private UnpackJar unpackJar;

    public StartupRunner(ImportDataService importDataService, UnpackJar unpackJar) {
        this.importDataService = importDataService;
        this.unpackJar = unpackJar;
    }

    @Override
    public void run(String... args) {
        try {
            if (!StringUtils.isEmpty(defaultJar)) {
                unpackJar.extra(defaultJar, TEMP_DIR_NAME, defaultJarInit);
                String tempPath = TEMP_DIR_NAME + PREFIX_SCRIPT_DB;
                List<String> serviceNames = getServiceName(tempPath + GROOVY_PATH);
                if (!CollectionUtils.isEmpty(serviceNames)) {
                    XmlUtils.resolver(tempPath + MAPPING_PATH);
                    if (!importDataService.updateGroovy(serviceNames, tempPath + GROOVY_PATH)) {
                        throw new Exception("初始化groovy脚本失败！");
                    }
                    if (!importDataService.importData(serviceNames, tempPath + INIT_PATH)) {
                        throw new Exception("初始化excel失败！");
                    }
                }
                logger.info("数据库初始化任务完成！");
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error("数据库初始化任务失败, message: {}, exception: ", e.getMessage(), e);
        }
    }

    private List<String> getServiceName(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("File does not exist!");
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
