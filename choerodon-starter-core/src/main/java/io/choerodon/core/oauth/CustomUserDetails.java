package io.choerodon.core.oauth;

import java.io.Serializable;
import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 定制的userDetail对象
 *
 * @author wuguokai
 * @author Eugen
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

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomUserDetails)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CustomUserDetails that = (CustomUserDetails) o;

        if (!userId.equals(that.userId)) {
            return false;
        }
        if (!email.equals(that.email)) {
            return false;
        }
        if (!timeZone.equals(that.timeZone)) {
            return false;
        }
        if (!language.equals(that.language)) {
            return false;
        }
        if (!isAdmin.equals(that.isAdmin)) {
            return false;
        }
        if (!organizationId.equals(that.organizationId)) {
            return false;
        }
        if (!clientId.equals(that.clientId)) {
            return false;
        }
        if (!clientName.equals(that.clientName)) {
            return false;
        }
        if (!clientAccessTokenValiditySeconds.equals(that.clientAccessTokenValiditySeconds)) {
            return false;
        }
        if (!clientAuthorizedGrantTypes.equals(that.clientAuthorizedGrantTypes)) {
            return false;
        }
        if (!clientAutoApproveScopes.equals(that.clientAutoApproveScopes)) {
            return false;
        }
        if (!clientRefreshTokenValiditySeconds.equals(that.clientRefreshTokenValiditySeconds)) {
            return false;
        }
        if (!clientRegisteredRedirectUri.equals(that.clientRegisteredRedirectUri)) {
            return false;
        }
        if (!clientResourceIds.equals(that.clientResourceIds)) {
            return false;
        }
        if (!clientScope.equals(that.clientScope)) {
            return false;
        }
        return additionInfo.equals(that.additionInfo);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + timeZone.hashCode();
        result = 31 * result + language.hashCode();
        result = 31 * result + organizationId.hashCode();
        result = 31 * result + additionInfo.hashCode();
        result = 31 * result + isAdmin.hashCode();
        result = 31 * result + clientId.hashCode();
        result = 31 * result + clientName.hashCode();
        result = 31 * result + clientScope.hashCode();
        result = 31 * result + clientResourceIds.hashCode();
        result = 31 * result + clientRegisteredRedirectUri.hashCode();
        result = 31 * result + clientRefreshTokenValiditySeconds.hashCode();
        result = 31 * result + clientAutoApproveScopes.hashCode();
        result = 31 * result + clientAuthorizedGrantTypes.hashCode();
        result = 31 * result + clientAccessTokenValiditySeconds.hashCode();
        return result;
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
