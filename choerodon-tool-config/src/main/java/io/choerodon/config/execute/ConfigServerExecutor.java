package io.choerodon.config.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.choerodon.config.builder.Builder;
import io.choerodon.config.builder.BuilderFactory;
import io.choerodon.config.domain.Service;
import io.choerodon.config.domain.ServiceConfig;
import io.choerodon.config.domain.ZuulRoute;
import io.choerodon.config.mapper.ServiceConfigMapper;
import io.choerodon.config.mapper.ServiceMapper;
import io.choerodon.config.mapper.ZuulRouteMapper;
import io.choerodon.config.utils.ConfigFileFormat;
import io.choerodon.config.utils.InitConfigProperties;
import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class ConfigServerExecutor extends AbstractExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServerExecutor.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private ServiceMapper serviceMapper;
    private ServiceConfigMapper serviceConfigMapper;
    private ZuulRouteMapper zuulRouteMapper;
    private static final String CONFIG_BY_TOOL = "工具生成";
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


    public ConfigServerExecutor(ServiceMapper serviceMapper,
                                ServiceConfigMapper serviceConfigMapper,
                                ZuulRouteMapper zuulRouteMapper) {
        this.serviceMapper = serviceMapper;
        this.serviceConfigMapper = serviceConfigMapper;
        this.zuulRouteMapper = zuulRouteMapper;
    }

    /**
     * 存储配置文件解析成Config对象，存储到数据库
     *
     * @param properties 配置
     * @param configFile 配置文件路径
     * @throws IOException 文件读写异常
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(InitConfigProperties properties, String configFile) throws IOException {
        String serviceName = properties.getService().getName();
        createService(serviceName);
        Map<String, Object> map = parseFileToMap(configFile);
        map = executeInternal(properties, map);
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
            serviceConfig.setConfigVersion(properties.getService().getVersion());
            if (serviceConfigMapper.insert(serviceConfig) != 1) {
                throw new CommonException("error.serviceConfig.insert");
            }
        } else {
            Map<String, Object> baseMap = objectMapper.readValue(serviceConfig.getValue(), Map.class);
            Map<String, Object> mergeMap = mergeMap(baseMap, map);
            String newJson = objectMapper.writeValueAsString(mergeMap);
            serviceConfig.setValue(newJson);
            serviceConfig.setConfigVersion(properties.getService().getVersion());
            if (serviceConfigMapper.updateByPrimaryKeySelective(serviceConfig) != 1) {
                throw new CommonException("error.serviceConfig.update");
            }
        }
        LOGGER.info("配置初始化完成");
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
     * 把除了zuul.route之外的数据键值对返回
     *
     * @param properties 配置类
     * @param map        整个配置文件的键值对集合
     * @return map
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeInternal(InitConfigProperties properties, Map<String, Object> map) {
        String serviceName = properties.getService().getName();
        if (Arrays.asList(properties.getGateway().getNames()).contains(serviceName)) {
            SortConfigMap sortConfigMap = sort(map);
            dataImport(sortConfigMap.zuulMap);
            return sortConfigMap.commonMap;
        }
        return map;
    }

    /**
     * 把zuulRoute属性对象存储到数据库
     *
     * @param map 服务路由信息的map
     */
    @SuppressWarnings("unchecked")
    private void dataImport(Map<String, Object> map) {
        try {
            Map<String, Object> normalMap = map.keySet().stream().collect(Collectors.toMap(
                    i -> i.substring(12),
                    map::get
            ));
            Builder builder = BuilderFactory.getBuilder(ConfigFileFormat.YML);
            String yml = builder.build(normalMap);
            List<ZuulRoute> existZuulRoutes = zuulRouteMapper.selectAll();
            final Map<String, ZuulRoute> existRouteNameWithIds = new HashMap<>();
            existZuulRoutes.forEach(t -> existRouteNameWithIds.put(t.getName(), t));
            LinkedHashMap<String, LinkedHashMap> routeMap = mapper.readValue(yml, LinkedHashMap.class);
            routeMapToZuulRoute(routeMap).forEach(i -> {
                ZuulRoute existRoute = existRouteNameWithIds.get(i.getName());
                if (existRoute != null) {
                    i.setId(existRoute.getId());
                    i.setObjectVersionNumber(existRoute.getObjectVersionNumber());
                    zuulRouteMapper.updateByPrimaryKeySelective(i);
                    return;
                }
                ZuulRoute pathRoute = new ZuulRoute();
                pathRoute.setPath(i.getPath());
                ZuulRoute zuulRoute = zuulRouteMapper.selectOne(pathRoute);
                if (zuulRoute != null) {
                    i.setId(zuulRoute.getId());
                    zuulRouteMapper.updateByPrimaryKeySelective(i);
                } else {
                    zuulRouteMapper.insert(i);
                }
            });
        } catch (IOException e) {
            LOGGER.error("api-gateway解析失败", e);
            throw new IllegalStateException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<ZuulRoute> routeMapToZuulRoute(LinkedHashMap<String, LinkedHashMap> routeMap) {
        return routeMap.keySet().stream().map(key -> {
            LinkedHashMap<String, Object> tmp = routeMap.get(key);
            ZuulRoute route = new ZuulRoute();
            route.setName(key);
            if (tmp.containsKey("path")) {
                route.setPath((String) tmp.get("path"));
            }
            if (tmp.containsKey("serviceId")) {
                route.setServiceId((String) tmp.get("serviceId"));
            }
            if (tmp.containsKey("url")) {
                route.setUrl((String) tmp.get("url"));
            }
            if (tmp.containsKey("sensitiveHeaders")) {
                route.setSensitiveHeaders((String) tmp.get("sensitiveHeaders"));
            }
            if (tmp.containsKey("retryable")) {
                route.setRetryable((Boolean) tmp.get("retryable"));
            }
            if (tmp.containsKey("customSensitiveHeaders")) {
                route.setCustomSensitiveHeaders((Boolean) tmp.get("customSensitiveHeaders"));
            }
            if (tmp.containsKey("stripPrefix")) {
                route.setStripPrefix((Boolean) tmp.get("stripPrefix"));
            }
            route.setBuiltIn(true);
            return route;
        }).collect(Collectors.toList());
    }
}
