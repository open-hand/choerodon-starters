package io.choerodon.core.notify;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 * 消息通知的类型
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
    public static NotifyType fromTypeName(String typeName) {
        for (NotifyType type : NotifyType.values()) {
            if (type.getValue().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getTypeName() {
        return this.typeName;
    }


}
