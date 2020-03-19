package io.choerodon.core.notify;

/**
 * @author scp
 * @date 2020/3/19
 * @description
 */
public class WebHookAndUserSendDTO extends WebHookSendDTO {
    /**
     * 执行者
     */
    private User user;

    public static class User {
        private String loginName;
        private String userName;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
