package org.springframework.cloud.config.client;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 存储路由实体类
 * @author zhipeng.zuo
 *      Created on 17-11-30.
 */
public class ZuulRoute {

    private String id;

    private String path;

    private String serviceId;

    private String url;

    private Boolean stripPrefix = true;

    private Boolean retryable;

    private String helperService;

    private Set<String> sensitiveHeaders = new LinkedHashSet<>();

    @JsonIgnore
    private String sensitiveHeadersJson;

    private boolean customSensitiveHeaders = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public Set<String> getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public boolean isCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public String getSensitiveHeadersJson() {
        return sensitiveHeadersJson;
    }

    public void setSensitiveHeadersJson(String sensitiveHeadersJson) {
        this.sensitiveHeadersJson = sensitiveHeadersJson;
    }

    public Boolean getStripPrefix() {
        return this.stripPrefix;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

    @Override
    public String toString() {
        return "ZuulRoute{"
                + "id='" + id + '\''
                + ", path='" + path + '\''
                + ", serviceId='" + serviceId + '\''
                + ", url='" + url + '\''
                + ", stripPrefix=" + stripPrefix
                + ", retryable=" + retryable
                + ", helperService='" + helperService + '\''
                + ", sensitiveHeaders=" + sensitiveHeaders
                + ", sensitiveHeadersJson='" + sensitiveHeadersJson + '\''
                + ", customSensitiveHeaders=" + customSensitiveHeaders
                + '}';
    }
}
