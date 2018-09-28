package io.choerodon.config.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.choerodon.config.builder.Builder;
import io.choerodon.config.builder.BuilderFactory;
import io.choerodon.config.domain.ZuulRoute;
import io.choerodon.config.mapper.ZuulRouteMapper;
import io.choerodon.config.utils.ConfigFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * api-gateway服务的执行器
 *
 * @author wuguokai
 */
public class ApiGatewayExecutor extends AbstractExector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayExecutor.class);
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private ZuulRouteMapper zuulRouteMapper;

    ApiGatewayExecutor(ApplicationContext applicationContext) {
        super(applicationContext);
        this.zuulRouteMapper = applicationContext.getBean(ZuulRouteMapper.class);
    }

    /**
     * 把除了zuul.route之外的数据键值对返回
     *
     * @param map 整个配置文件的键值对集合
     * @return map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeInternal(Map<String, Object> map) {
        Set<String> extraKeySet = map.keySet().stream()
                .filter(o -> o.startsWith("zuul.route")).collect(Collectors.toSet());
        Map<String, Object> extraMap = extraKeySet.stream()
                .filter(i -> i != null && map.get(i) != null).collect(Collectors.toMap(i -> i, map::get));
        Map<String, Object> commonMap = map.keySet().stream()
                .filter(i -> !extraKeySet.contains(i)).collect(Collectors.toMap(i -> i, map::get));
        dataImport(extraMap);
        return commonMap;
    }

    /**
     * 把zuulRoute属性对象存储到数据库
     *
     * @param map 服务路由信息的map
     */
    private void dataImport(Map<String, Object> map) {
        try {
            Map<String, Object> normalMap = map.keySet().stream().collect(Collectors.toMap(
                    i -> i.substring(12),
                    map::get
            ));
            Builder builder = BuilderFactory.getBuilder(ConfigFileFormat.YML);
            String yml = builder.build(normalMap);
            List<ZuulRoute> existZuulRoutes = zuulRouteMapper.selectAll();
            Set<String> existRouteIds = existZuulRoutes.stream()
                    .map(ZuulRoute::getName).collect(Collectors.toSet());
            LinkedHashMap<String, LinkedHashMap> routeMap = mapper.readValue(yml, LinkedHashMap.class);
            List<ZuulRoute> addOrUpdateZuulRoutes = routeMapToZuulRoute(routeMap).stream()
                    .filter(i -> !existRouteIds.contains(i.getName())).collect(Collectors.toList());
            addOrUpdateZuulRoutes.forEach(i -> {
                i.setBuiltIn(true);
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
            LOGGER.error("api-gateway解析失败");
            throw new IllegalStateException(e.getMessage());
        }
    }

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
            return route;
        }).collect(Collectors.toList());
    }

//    public void
}
