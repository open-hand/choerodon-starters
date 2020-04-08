package io.choerodon.config;

import io.choerodon.config.execute.Executor;
import io.choerodon.config.utils.FileUtil;
import io.choerodon.config.utils.InitConfigProperties;
import io.choerodon.mybatis.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 读取传入的配置信息，并执行初始化配置操作
 *
 * @author wuguokai
 */
@Component
public class ConfigToolExecute implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigToolExecute.class);
    private static final String TEMP_DIR_NAME = "temp/";

    private FileUtil fileUtil = new FileUtil();

    private InitConfigProperties properties;

    private Executor executor;


    public ConfigToolExecute(InitConfigProperties initConfigProperties, Executor executor) {
        this.properties = initConfigProperties;
        this.executor = executor;
    }

    /**
     * 运行初始化配置的入口
     *
     * @param strings 参数
     */
    @Override
    public void run(String... strings) {
        try {
            if (StringUtil.isEmpty(properties.getService().getName())) {
                throw new IllegalStateException("请传入应用名service.name和service.version");
            }
            String dir = ".";
            if (properties.getConfig().getJar() != null) {
                fileUtil.extra(properties.getConfig().getJar(), TEMP_DIR_NAME);
                dir = TEMP_DIR_NAME;
            }
            LOGGER.info("根据指定文件进行配置初始化: {}", properties.getService().getName());
            List<File> fileList = fileUtil.getDirRecursive(new File(dir));
            String absConfigFilePath = fileUtil.getDirInJar(fileList, properties.getConfig().getFile());
            executor.execute(properties, absConfigFilePath);
            System.exit(0);
        } catch (Exception e) {
            LOGGER.error("初始化配置失败：", e);
            System.exit(1);
        }
    }

}
