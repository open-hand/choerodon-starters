package io.choerodon.core.notify;

import java.util.Date;

import com.google.gson.JsonObject;


/**
 * @author scp
 * @date 2020/3/19
 * @description
 */
public class WebHookSendDTO {
    /**
     * 发送的send_setting的code
     */
    private String objectKind;

    /**
     * 发送的消息名称
     */
    private String eventName;

    /**
     * 发送消息json对象
     */
    private JsonObject objectAttributes;

    /**
     * 执行时间
     */
    private Date createdAt;

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

    public String getObjectKind() {
        return objectKind;
    }

    public void setObjectKind(String objectKind) {
        this.objectKind = objectKind;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public JsonObject getObjectAttributes() {
        return objectAttributes;
    }

    public void setObjectAttributes(JsonObject objectAttributes) {
        this.objectAttributes = objectAttributes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
