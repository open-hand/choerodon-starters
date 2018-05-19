package io.choerodon.config.execute;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.config.domain.ServiceConfig;
import io.choerodon.config.domain.Service;
import io.choerodon.config.mapper.ServiceConfigMapper;
import io.choerodon.config.mapper.ServiceMapper;
import io.choerodon.config.parser.Parser;
import io.choerodon.config.parser.ParserFactory;
import io.choerodon.config.utils.ConfigFileFormat;
import io.choerodon.config.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

/**
 * 存储配置文件执行器
 *
 * @author wuguokai
 */
public abstract class AbstractExector implements Executor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExector.class);
    private FileUtil fileUtil = new FileUtil();
    private ObjectMapper objectMapper = new ObjectMapper();
    private ServiceMapper serviceMapper;
    private ServiceConfigMapper serviceConfigMapper;

    AbstractExector(ApplicationContext applicationContext) {
        this.serviceMapper = applicationContext.getBean(ServiceMapper.class);
        this.serviceConfigMapper = applicationContext.getBean(ServiceConfigMapper.class);
    }

    /**
     * 存储配置文件解析成Config对象，存储到数据库
     *
     * @param serviceName    服务名称
     * @param serviceVersion 服务版本
     * @param configFile     配置文件路径
     * @throws IOException 文件读写异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(String serviceName, String serviceVersion, String configFile) throws IOException {
        File file = new File(configFile);
        ConfigFileFormat fileFormat = ConfigFileFormat.fromString(fileUtil.getFileExt(file));
        Parser parser = ParserFactory.getParser(fileFormat);
        Map<String, Object> map = parser.parse(file);

        map = executeInternal(map);

        Service service = new Service();
        service.setName(serviceName);
        service = serviceMapper.selectOne(service);

        ServiceConfig queryServiceConfig = new ServiceConfig();
        queryServiceConfig.setServiceId(service.getId());
        queryServiceConfig.setConfigVersion(serviceVersion);
        ServiceConfig serviceConfig = serviceConfigMapper.selectOne(queryServiceConfig);
        if (serviceConfig == null) {
            serviceConfig = serviceConfigMapper.selectOneByServiceDefault(serviceName);

            Map<String, Object> baseMap = serviceConfig != null ? objectMapper.readValue(serviceConfig.getValue(), Map.class) :
                    new LinkedHashMap<>();
            Map<String, Object> mergeMap = mergeMap(baseMap, map);
            String newJson = objectMapper.writeValueAsString(mergeMap);
            ServiceConfig newServiceConfig = new ServiceConfig();
            newServiceConfig.setName(serviceName+"."+System.currentTimeMillis());
            newServiceConfig.setConfigVersion(serviceVersion);
            newServiceConfig.setServiceId(service.getId());
            newServiceConfig.setDefault(true);
            newServiceConfig.setPublicTime(new Date(System.currentTimeMillis()));
            newServiceConfig.setValue(newJson);
            newServiceConfig.setSource("工具生成");
            if (serviceConfig != null) {
                serviceConfig.setDefault(false);
                serviceConfigMapper.updateByPrimaryKey(serviceConfig);
            }
            serviceConfigMapper.insert(newServiceConfig);
            LOGGER.warn("{} - {} 配置初始化完成", serviceName, serviceVersion);
        } else {
            LOGGER.warn("{} - {} 已有配置载入", serviceName, serviceVersion);
        }
    }

    /**
     * 合并两个map成一个
     *
     * @param baseMap 基础map
     * @param newMap  新的map
     * @return mergeMap 合并之后的map
     */
    private Map<String, Object> mergeMap(Map<String, Object> baseMap, Map<String, Object> newMap) {
        Map<String, Object> mergeMap = new LinkedHashMap<>();
        Set<String> baseKeySet = baseMap.keySet();
        for (Map.Entry<String, Object> entry : baseMap.entrySet()) {
            mergeMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : newMap.entrySet()) {
            String newKey = entry.getKey();
            if (!baseKeySet.contains(newKey)) {
                mergeMap.put(newKey, newMap.getOrDefault(newKey, ""));
            }
        }
        return mergeMap;
    }

    /**
     * 把键值对map中去除zuul.route部分存储到zuul_route表中
     *
     * @param map 解析完成的配置文件键值对map
     * @return map
     */
    protected abstract Map<String, Object> executeInternal(Map<String, Object> map);
}
