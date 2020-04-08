package io.choerodon.config.domain;

import javax.persistence.*;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 数据库zuul_route表实体对象
 *
 * @author wuguokai
 */
@Table(name = "mgmt_route")
public class ZuulRoute extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String path;

    private String serviceId;

    private String url;

    private Boolean stripPrefix;

    private Boolean retryable;

    private String sensitiveHeaders;

    private Boolean customSensitiveHeaders;

    private String helperService;

    @Column(name = "is_built_in")
    private Boolean builtIn;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getStripPrefix() {
        return stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public Boolean getCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public String getHelperService() {
        return helperService;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(Boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }
}
