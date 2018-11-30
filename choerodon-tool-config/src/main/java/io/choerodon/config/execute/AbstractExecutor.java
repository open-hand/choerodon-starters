package io.choerodon.config.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.config.domain.Service;
import io.choerodon.config.domain.ServiceConfig;
import io.choerodon.config.mapper.*;
import io.choerodon.config.parser.Parser;
import io.choerodon.config.parser.ParserFactory;
import io.choerodon.config.utils.ConfigFileFormat;
import io.choerodon.config.utils.FileUtil;
import io.choerodon.core.exception.CommonException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 存储配置文件执行器
 *
 * @author wuguokai
 */
public abstract class AbstractExecutor implements Executor {

    private FileUtil fileUtil = new FileUtil();
    private ObjectMapper objectMapper = new ObjectMapper();
    private ServiceMapper serviceMapper;
    private ServiceConfigMapper serviceConfigMapper;
    private static final String CONFIG_BY_TOOL = "工具生成";

    AbstractExecutor(ApplicationContext applicationContext) {
        this.serviceMapper = applicationContext.getBean(ServiceMapper.class);
        this.serviceConfigMapper = applicationContext.getBean(ServiceConfigMapper.class);
    }

    /**
     * 存储配置文件解析成Config对象，存储到数据库
     *
     * @param serviceName 服务名称
     * @param configFile  配置文件路径
     * @throws IOException 文件读写异常
     */
    @SuppressWarnings("unchecked")
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
        queryServiceConfig.setDefault(true);
        ServiceConfig serviceConfig = serviceConfigMapper.selectOne(queryServiceConfig);

        if (serviceConfig == null) {
            serviceConfig = new ServiceConfig(serviceName + "." + System.currentTimeMillis(), true, service.getId(),
                    objectMapper.writeValueAsString(map), CONFIG_BY_TOOL, new Date(System.currentTimeMillis()));
            serviceConfig.setConfigVersion(serviceVersion);
            if (serviceConfigMapper.insert(serviceConfig) != 1) {
                throw new CommonException("error.serviceConfig.insert");
            }
        } else {
            Map<String, Object> baseMap = objectMapper.readValue(serviceConfig.getValue(), Map.class);
            Map<String, Object> mergeMap = mergeMap(baseMap, map);
            String newJson = objectMapper.writeValueAsString(mergeMap);
            serviceConfig.setValue(newJson);
            serviceConfig.setConfigVersion(serviceVersion);
            if (serviceConfigMapper.updateByPrimaryKeySelective(serviceConfig) != 1) {
                throw new CommonException("error.serviceConfig.update");
            }
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
