package io.choerodon.core.notify;

public enum Level {
    SITE("site"),
    ORGANIZATION("organization");

    private String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
