package io.choerodon.oauth.core.password.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author wuguokai
 */
@ModifyAudit
@VersionAudit
@Table(name = "oauth_login_attempt_times")
public class BaseLoginAttemptTimesDO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Integer loginAttemptTimes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLoginAttemptTimes() {
        return loginAttemptTimes;
    }

    public void setLoginAttemptTimes(Integer loginAttemptTimes) {
        this.loginAttemptTimes = loginAttemptTimes;
    }
}
