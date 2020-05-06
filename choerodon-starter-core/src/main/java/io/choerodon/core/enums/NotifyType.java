package io.choerodon.core.enums;

/**
 * @author scp
 * @date 2020/5/6
 * @description
 */
public enum NotifyType {
    EMAIL("email"),
    WEBHOOK("webhook"),
    WEBHOOK_JSON("webhook-json"),
    SMS("sms"),
    PM("pm");

    private String value;

    private NotifyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}