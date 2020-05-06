package io.choerodon.core.enums;

/**
 * @author scp
 * @date 2020/4/29
 * @description
 */
public enum ServiceNotifyType {
    AGILE_NOTIFY("agile"),
    DEVOPS_NOTIFY("devops"),
    RESOURCE_DELETE_NOTIFY("resourceDelete"),
    DEFAULT_NOTIFY("default");

    private String typeName;

    ServiceNotifyType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static ServiceNotifyType fromTypeName(String typeName) {
        for (ServiceNotifyType type : ServiceNotifyType.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
