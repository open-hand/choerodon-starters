package io.choerodon.core.notify;

import java.util.List;
import java.util.Map;

public class NoticeSendDTO {

    /**
     * 发送的业务类型code
     */
    private String code;

    /**
     * 发送消息的用户
     */
    private User fromUser;

    /**
     * 目标用户
     */
    private List<User> targetUsers;
    /**
     * 模版渲染参数
     * key:模版中的${}字段名
     * value:渲染的值
     */
    private Map<String, Object> params;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<User> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(List<User> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public static class User {

        private Long id;

        private String loginName;

        private String realName;

        private String imageUrl;

        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public User(Long id, String loginName, String realName, String imageUrl, String email) {
            this.id = id;
            this.loginName = loginName;
            this.realName = realName;
            this.imageUrl = imageUrl;
            this.email = email;
        }

        public User(Long id, String email) {
            this.id = id;
            this.email = email;
        }

        public User() {
        }
    }
}
