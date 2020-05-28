package io.choerodon.core.enums;

/**
 * @author scp
 * @date 2020/5/13
 * @description
 */
public enum MessageAdditionalType {
    PARAM_PROJECT_ID("projectId"),
    PARAM_ENV_ID("envId"),
    PARAM_TENANT_ID("tenantId");

    private String typeName;

    MessageAdditionalType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static MessageAdditionalType fromTypeName(String typeName) {
        for (MessageAdditionalType type : MessageAdditionalType.values()) {
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
