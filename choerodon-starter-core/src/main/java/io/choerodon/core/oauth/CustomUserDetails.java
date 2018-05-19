package io.choerodon.core.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 定制的userDetail对象
 *
 * @author wuguokai
 */
public class CustomUserDetails extends User {

    private Long userId;

    private String email;

    private String timeZone;

    private String language;

    private Long organizationId;

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
        if (!organizationId.equals(that.organizationId)) {
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
        return result;
    }
}
