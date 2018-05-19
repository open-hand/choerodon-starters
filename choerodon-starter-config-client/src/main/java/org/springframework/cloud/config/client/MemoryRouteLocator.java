package org.springframework.cloud.config.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

/**
 * 刷新路由
 * @author zhipeng.zuo
 *      Created on 17-12-5.
 */
public class MemoryRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    private static Map<String, ZuulProperties.ZuulRoute> zuulRouteHashMap = new HashMap<>();

    private static final String PATH_SPE = "/";

    private ZuulProperties properties;

    public MemoryRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>();
        routesMap.putAll(zuulRouteHashMap);
        LinkedHashMap<String, ZuulProperties.ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            if (!path.startsWith("/")) {
                path = PATH_SPE + path;
            }
            if (StringUtils.hasText(this.properties.getPrefix())) {
                path = this.properties.getPrefix() + path;
                if (!path.startsWith("/")) {
                    path = PATH_SPE + path;
                }
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    public static Map<String, ZuulProperties.ZuulRoute> getMap() {
        return zuulRouteHashMap;
    }

    public static void setMap(Map<String, ZuulProperties.ZuulRoute> map) {
        zuulRouteHashMap = map;
    }
}
