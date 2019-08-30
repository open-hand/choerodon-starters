package io.choerodon.web.core.impl;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.web.core.IRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 默认的 IRequest 实现.
 *
 * @author shengyang.zhou@hand-china.com
 */
public class ServiceRequest implements IRequest {


    private static final String ATTR_USER_ID = "_userId";

    private static final String ATTR_ROLE_ID = "_roleId";

    private static final String ATTR_COMPANY_ID = "_companyId";

    private static final String ATTR_LOCALE = "_locale";

    private static final long serialVersionUID = 3699668645012922404L;

    private Long userId = -1L;
    private Long roleId = -1L;
    private Long[] roleIds = {};
    private Long companyId = -1L;
    private String locale = Locale.getDefault().toString();
    private String employeeCode;
    private String userName;

    @JsonIgnore
    private Map<String, Object> attributeMap = new HashMap<>();

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
        setAttribute(ATTR_USER_ID, userId);
    }

    @Override
    public String getEmployeeCode() {
        return employeeCode;
    }

    @Override
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
        setAttribute(ATTR_LOCALE, locale);
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public Long[] getAllRoleId() {
        return roleIds;
    }

    @Override
    public void setAllRoleId(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
        setAttribute(ATTR_ROLE_ID, roleId);
    }

    @Override
    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
        setAttribute(ATTR_COMPANY_ID, companyId);
    }

    @Override
    @SuppressWarnings("unchecked")
    @JsonAnyGetter
    public <T> T getAttribute(String name) {
        return (T) attributeMap.get(name);
    }

    @Override
    @JsonAnySetter
    public void setAttribute(String name, Object value) {
        attributeMap.put(name, value);
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    @Override
    @JsonIgnore
    public Set<String> getAttributeNames() {
        return attributeMap.keySet();
    }
}
