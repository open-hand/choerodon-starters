package io.choerodon.mapper;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.helper.SelectKeyGenerator;

import javax.persistence.*;
import java.util.List;

/**
 * @author superlee
 */
@ModifyAudit
@VersionAudit
@MultiLanguage
@Table(name = "iam_role")
public class RoleDO extends AuditDomain {
    @Id
    @GeneratedValue
//    @OrderBy
    private Long id;
    @MultiLanguageField
    private String name;
    private String code;
    private String description;
    @Column(name = "fd_level")
    private String level;
    @Column(name = "is_enabled")
    private Boolean enabled;
    @Column(name = "is_modified")
    private Boolean modified;
    @Column(name = "is_enable_forbidden")
    private Boolean enableForbidden;
    @Column(name = "is_built_in")
    private Boolean builtIn;
    @Column(name = "is_assignable")
    private Boolean assignable;

    @Transient
    private String organizationName;

    @Transient
    private String projectName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getModified() {
        return modified;
    }

    public void setModified(Boolean modified) {
        this.modified = modified;
    }

    public Boolean getEnableForbidden() {
        return enableForbidden;
    }

    public void setEnableForbidden(Boolean enableForbidden) {
        this.enableForbidden = enableForbidden;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public Boolean getAssignable() {
        return assignable;
    }

    public void setAssignable(Boolean assignable) {
        this.assignable = assignable;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
