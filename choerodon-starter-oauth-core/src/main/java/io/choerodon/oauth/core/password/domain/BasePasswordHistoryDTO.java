package io.choerodon.oauth.core.password.domain;

import javax.persistence.*;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author wuguokai
 */
@Table(name = "oauth_password_history")
public class BasePasswordHistoryDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(name = "hash_password")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
