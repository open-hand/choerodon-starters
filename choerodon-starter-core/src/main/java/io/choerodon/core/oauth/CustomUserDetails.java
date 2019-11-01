package io.choerodon.core.oauth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;
import java.util.*;

/**
 * 定制的userDetail对象
 *
 * @author wuguokai
 * @author Eugen
 * @author zongw.lee@gmail.com
 */
public class CustomUserDetails extends User implements Serializable {
    private static final long serialVersionUID = -3762281463683847665L;

    private Long userId;

    private String email;

    private String timeZone;

    private String language;

    private Long organizationId;

    private Boolean isAdmin;

    private Long clientId;

    private String clientName;

    private Set<String> clientAuthorizedGrantTypes;

    private Set<String> clientResourceIds;

    private Set<String> clientScope;

    private Set<String> clientRegisteredRedirectUri;

    private Integer clientAccessTokenValiditySeconds;

    private Integer clientRefreshTokenValiditySeconds;

    private Set<String> clientAutoApproveScopes;

    private transient Map<String, Object> additionInfo;

    private String routeRuleCode;

    public CustomUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    @JsonCreator
    public CustomUserDetails(@JsonProperty("username") String username,
                             @JsonProperty("password") String password) {
        super(username, password, Collections.emptyList());
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Map<String, Object> getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(Map<String, Object> additionInfo) {
        this.additionInfo = additionInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Set<String> getClientAuthorizedGrantTypes() {
        return clientAuthorizedGrantTypes;
    }

    public void setClientAuthorizedGrantTypes(Collection<String> clientAuthorizedGrantTypes) {
        this.clientAuthorizedGrantTypes = clientAuthorizedGrantTypes == null ? Collections
                .<String>emptySet() : new LinkedHashSet<String>(clientAuthorizedGrantTypes);
    }

    public Set<String> getClientResourceIds() {
        return clientResourceIds;
    }

    public void setClientResourceIds(Collection<String> clientResourceIds) {
        this.clientResourceIds = clientResourceIds == null ? Collections
                .<String>emptySet() : new LinkedHashSet<String>(clientResourceIds);
    }

    public Set<String> getClientScope() {
        return clientScope;
    }

    public void setClientScope(Collection<String> clientScope) {
        this.clientScope = clientScope == null ? Collections.<String>emptySet()
                : new LinkedHashSet<String>(clientScope);
    }

    public Set<String> getClientRegisteredRedirectUri() {
        return clientRegisteredRedirectUri;
    }

    public void setClientRegisteredRedirectUri(Collection<String> clientRegisteredRedirectUri) {
        this.clientRegisteredRedirectUri = clientRegisteredRedirectUri == null ? null
                : new LinkedHashSet<String>(clientRegisteredRedirectUri);
    }

    public Integer getClientAccessTokenValiditySeconds() {
        return clientAccessTokenValiditySeconds;
    }

    public void setClientAccessTokenValiditySeconds(Integer clientAccessTokenValiditySeconds) {
        this.clientAccessTokenValiditySeconds = clientAccessTokenValiditySeconds;
    }

    public Integer getClientRefreshTokenValiditySeconds() {
        return clientRefreshTokenValiditySeconds;
    }

    public void setClientRefreshTokenValiditySeconds(Integer clientRefreshTokenValiditySeconds) {
        this.clientRefreshTokenValiditySeconds = clientRefreshTokenValiditySeconds;
    }

    public Set<String> getClientAutoApproveScopes() {
        return clientAutoApproveScopes;
    }

    public void setClientAutoApproveScopes(Collection<String> clientAutoApproveScopes) {
        this.clientAutoApproveScopes = clientAutoApproveScopes == null ? null
                : new LinkedHashSet<String>(clientAutoApproveScopes);
    }

    public String getRouteRuleCode() {
        return routeRuleCode;
    }

    public void setRouteRuleCode(String routeRuleCode) {
        this.routeRuleCode = routeRuleCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(email, that.email) &&
                Objects.equals(timeZone, that.timeZone) &&
                Objects.equals(language, that.language) &&
                Objects.equals(organizationId, that.organizationId) &&
                Objects.equals(isAdmin, that.isAdmin) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientName, that.clientName) &&
                Objects.equals(clientAuthorizedGrantTypes, that.clientAuthorizedGrantTypes) &&
                Objects.equals(clientResourceIds, that.clientResourceIds) &&
                Objects.equals(clientScope, that.clientScope) &&
                Objects.equals(clientRegisteredRedirectUri, that.clientRegisteredRedirectUri) &&
                Objects.equals(clientAccessTokenValiditySeconds, that.clientAccessTokenValiditySeconds) &&
                Objects.equals(clientRefreshTokenValiditySeconds, that.clientRefreshTokenValiditySeconds) &&
                Objects.equals(clientAutoApproveScopes, that.clientAutoApproveScopes) &&
                Objects.equals(additionInfo, that.additionInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, email, timeZone, language, organizationId, isAdmin, clientId, clientName, clientAuthorizedGrantTypes, clientResourceIds, clientScope, clientRegisteredRedirectUri, clientAccessTokenValiditySeconds, clientRefreshTokenValiditySeconds, clientAutoApproveScopes, additionInfo);
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", language='" + language + '\'' +
                ", organizationId=" + organizationId +
                ", isAdmin=" + isAdmin +
                ", clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", clientAuthorizedGrantTypes=" + clientAuthorizedGrantTypes +
                ", clientResourceIds=" + clientResourceIds +
                ", clientScope=" + clientScope +
                ", clientRegisteredRedirectUri=" + clientRegisteredRedirectUri +
                ", clientAccessTokenValiditySeconds=" + clientAccessTokenValiditySeconds +
                ", clientRefreshTokenValiditySeconds=" + clientRefreshTokenValiditySeconds +
                ", clientAutoApproveScopes=" + clientAutoApproveScopes +
                ", additionInfo=" + additionInfo +
                '}';
    }
}
