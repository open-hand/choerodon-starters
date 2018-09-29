package io.choerodon.config;

import io.choerodon.config.domain.Service;
import io.choerodon.config.execute.Executor;
import io.choerodon.config.execute.ExecutorFactory;
import io.choerodon.config.mapper.ServiceMapper;
import io.choerodon.config.utils.FileUtil;
import io.choerodon.config.utils.GatewayProperties;
import io.choerodon.config.utils.ServiceType;
import io.choerodon.mybatis.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${config.file:application-default.yml}")
    private String configFileName;

    @Value("${service.name:#{null}}")
    private String serviceName;

    @Value("${config.jar:#{null}}")
    private String jar;

    @Value("${service.version:#{null}}")
    private String serviceVersion;

    private FileUtil fileUtil = new FileUtil();

    private ServiceMapper serviceMapper;

    private ExecutorFactory executorFactory;

    private GatewayProperties gatewayProperties;


    /**
     * 构造器
     *
     * @param serviceMapper     serviceMapper
     * @param executorFactory   executorFactory
     * @param gatewayProperties gatewayProperties
     */
    public ConfigToolExecute(ServiceMapper serviceMapper, ExecutorFactory executorFactory, GatewayProperties gatewayProperties) {
        this.serviceMapper = serviceMapper;
        this.executorFactory = executorFactory;
        this.gatewayProperties = gatewayProperties;
    }

    /**
     * 运行初始化配置的入口
     *
     * @param strings 参数
     */
    @Override
    public void run(String... strings) {
        try {
            if (StringUtil.isEmpty(serviceName)) {
                throw new IllegalStateException("请传入应用名service.name和service.version");
            }
            String dir = ".";
            createService(serviceName);
            if (jar != null) {
                fileUtil.extra(jar, TEMP_DIR_NAME);
                dir = TEMP_DIR_NAME;
            }
            LOGGER.info("根据指定文件进行配置初始化: {}", serviceName);
            List<File> fileList = fileUtil.getDirRecursive(new File(dir));
            String absConfigFilePath = fileUtil.getDirInJar(fileList, configFileName);
            ServiceType type = judgeType(serviceName);
            Executor executor = executorFactory.getExecutor(type);
            executor.execute(serviceName, serviceVersion, absConfigFilePath);
            System.exit(0);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 初始化服务数据
     *
     * @param serviceName 应用名
     */
    private void createService(String serviceName) {
        Service service = new Service();
        service.setName(serviceName);
        if (serviceMapper.selectOne(service) == null) {
            serviceMapper.insert(service);
        }
    }

    /**
     * 根据注入的gateway的名字判断服务类型
     *
     * @param serviceName 服务名
     * @return 服务类型枚举类
     */
    private ServiceType judgeType(String serviceName) {
        for (int i = 0; i < gatewayProperties.getNames().length; i++) {
            if (serviceName.equals(gatewayProperties.getNames()[i])) {
                return ServiceType.fromString(0);
            }
        }
        return ServiceType.fromString(1);
    }
}
