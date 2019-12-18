package io.choerodon.core.notify;

import java.util.List;
import java.util.Map;

public class NoticeSendDTO {

    /**
     * 发送邮件
     */
    public static final String EMAIL = "email";

    /**
     * 发送站内信
     */
    public static final String SITE_MESSAGE = "siteMessage";

    /**
     * 发送短信
     */
    public static final String SMS = "sms";

    /**
     * 发送WebHook消息
     */
    public static final String WEB_HOOK = "WebHook";

    /**
     * 发送的业务类型code
     */
    private String code;

    /**
     * 事件名
     */
    private String eventName;

    /**
     * 环境ID
     */
    private Long envId;

    /**
     * 通知类型
     */
    private String notifyType;
    /**
     * 触发发送通知的组织或项目id，如果是site层，则不传或传0
     */
    private Long sourceId;

    /**
     * 发送者(目前用于发送站内信的发送者字段)
     */
    private User fromUser;

    /**
     * 自定义发送类型
     */
    private List<String> customizedSendingTypes;

    /**
     * 目标用户
     * (如果user中email字段不为空，则发送邮件至email)
     * 如果user中email为null，但是id不为空，查询该id对应的用户，发送至对应的邮箱处
     */
    private List<User> targetUsers;

    /**
     * 模版渲染参数(标题和内容渲染都在此参数中)
     */
    private Map<String, Object> params;

    public static class User {
        private Long id;
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
    }

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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public List<User> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(List<User> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public List<String> getCustomizedSendingTypes() {
        return customizedSendingTypes;
    }

    public void setCustomizedSendingTypes(List<String> customizedSendingTypes) {
        this.customizedSendingTypes = customizedSendingTypes;
    }

    public boolean isSendingEmail() {
        if (this.customizedSendingTypes == null) {
            return false;
        }
        return customizedSendingTypes.contains(EMAIL);
    }

    public boolean isSendingSiteMessage() {
        if (this.customizedSendingTypes == null) {
            return false;
        }
        return customizedSendingTypes.contains(SITE_MESSAGE);
    }

    public boolean isSendingSMS() {
        if (this.customizedSendingTypes == null) {
            return false;
        }
        return customizedSendingTypes.contains(SMS);
    }

    public boolean isSendingWebHook() {
        if (this.customizedSendingTypes == null){
            return false;
        }
        return customizedSendingTypes.contains(WEB_HOOK);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }
}
