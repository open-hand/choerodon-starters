package io.choerodon.core.notify;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;




/**
 * @author scp
 * @date 2020/3/19
 * @description
 */
public class WebHookJsonSendDTO {
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
    private JSONObject objectAttributes;

    /**
     * 执行时间
     */
    private Date createdAt;

    /**
     * 执行者
     */
    private User user;

    public WebHookJsonSendDTO(String objectKind, String eventName, JSONObject objectAttributes, Date createdAt, User user) {
        this.objectKind = objectKind;
        this.eventName = eventName;
        this.objectAttributes = objectAttributes;
        this.createdAt = createdAt;
        this.user = user;
    }

    public WebHookJsonSendDTO() {
    }

    public static class User {
        private String loginName;
        private String userName;

        public User() {
        }

        public User(String loginName, String userName) {
            this.loginName = loginName;
            this.userName = userName;
        }

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

    public JSONObject getObjectAttributes() {
        return objectAttributes;
    }

    public void setObjectAttributes(JSONObject objectAttributes) {
        this.objectAttributes = objectAttributes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
