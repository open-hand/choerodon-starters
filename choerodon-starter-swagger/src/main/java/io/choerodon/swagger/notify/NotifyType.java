package io.choerodon.swagger.notify;

/**
 * @author dengyouquan
 **/
public enum NotifyType {
    EMAIL("email"),
    SMS("sms"),
    PM("pm");

    private String value;

    NotifyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
