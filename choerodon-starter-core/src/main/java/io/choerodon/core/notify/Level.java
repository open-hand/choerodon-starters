package io.choerodon.core.notify;

public enum Level {
    SITE("site"),
    ORGANIZATION("organization"),
    PROJECT("project");

    private String value;

    Level(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
