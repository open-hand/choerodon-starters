package io.choerodon.oauth.core.password.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author wuguokai
 */
@Table(name = "oauth_login_attempt_times")
public class BaseLoginAttemptTimesDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
