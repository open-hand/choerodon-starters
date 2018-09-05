package io.choerodon.asgard.quartz;

public enum ParamType {
    BOOLEAN("Boolean"),
    INTEGER("Integer"),
    LONG("Long"),
    BYTE("Byte"),
    SHORT("Short"),
    CHARACTER("Character"),
    FLOAT("Float"),
    DOUBLE("Double"),
    STRING("String"),
    ;

    private String value;

    ParamType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
