package io.choerodon.asgard.schedule;

public enum ParamType {
    BOOLEAN("Boolean"),
    INTEGER("Integer"),
    LONG("Long"),
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

    public static ParamType getParamTypeByValue(final String value) {
        for (ParamType paramType : ParamType.values()) {
            if (paramType.value.equals(value)) {
                return paramType;
            }
        }
        return null;
    }

}
