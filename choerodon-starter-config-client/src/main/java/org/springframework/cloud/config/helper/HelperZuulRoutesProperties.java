package org.springframework.cloud.config.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.client.ZuulRoute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhipeng.zuo
 * 2018/1/24
 */
@ConfigurationProperties("zuul")
public class HelperZuulRoutesProperties {

    private Map<String, ZuulRoute> routes = new LinkedHashMap<>();

    public Map<String, ZuulRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, ZuulRoute> routes) {
        this.routes = routes;
    }
}