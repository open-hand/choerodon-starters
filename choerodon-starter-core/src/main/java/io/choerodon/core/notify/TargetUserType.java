package io.choerodon.core.notify;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mr.Wang
 * Date: 2019/12/4
 */
public enum TargetUserType {
    /**
     * 报告人
     */
    REPORTER("reporter"),
    /**
     * 经办人
     */
    ASSIGNEE("assignee"),
    /**
     * 指定用户
     */
    SPECIFIER("specifier"),
    /**
     * 创建者
     */
    CREATOR("creator"),
    /**
     * 应用服务权限拥有者
     */
    APPLICATION_SERVICE_PERMISSION_OWNER("applicationServicePermissionOwner"),
    /**
     * 代码提交者
     */
    CODE_SUBMITTEDER("codeSubmitter"),
    /**
     * 实例部署人员
     */
    INSTANCE_DEPLOYER("instanceDeployer"),
    /**
     * 流水线触发者
     */
    PIPELINE_TRIGGERS("pipelineTriggers"),

    /**
     * 操作者
     */
    HANDLER("handler"),

    /**
     * 项目所有者
     */
    PROJECT_OWNER("projectOwner");

    public static final String TARGET_HANDLER = "handler";
    public static final String TARGET_USER_REPORTER = "reporter";
    public static final String TARGET_USER_ASSIGNEE = "assignee";
    public static final String TARGET_USER_SPECIFIER = "specifier";
    public static final String TARGET_PROJECT_OWNER = "projectOwner";
    public static final String TARGET_USER_CREATOR = "creator";
    public static final String TARGET_USER_APPLICATION_SERVICE_PERMISSION_OWNER = "applicationServicePermissionOwner";
    public static final String TARGET_USER_CODE_SUBMITTER = "codeSubmitter";
    public static final String TARGET_USER_INSTANCE_DEPLOYER = "instanceDeployer";
    public static final String TARGET_USER_PIPELINE_TRIGGERSS = "pipelineTriggers";
    private String typeName;

    TargetUserType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static TargetUserType fromTypeName(String typeName) {
        for (TargetUserType type : TargetUserType.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static Map<String, String> nameMapping = new HashMap<>(8);

    static {
        nameMapping.put(TargetUserType.APPLICATION_SERVICE_PERMISSION_OWNER.getTypeName(), "应用服务权限拥有者");
        nameMapping.put(TargetUserType.PIPELINE_TRIGGERS.getTypeName(), "流水线触发者");
        nameMapping.put(TargetUserType.ASSIGNEE.getTypeName(), "经办人");
        nameMapping.put(TargetUserType.CODE_SUBMITTEDER.getTypeName(), "代码提交者");
        nameMapping.put(TargetUserType.CREATOR.getTypeName(), "创建者");
        nameMapping.put(TargetUserType.INSTANCE_DEPLOYER.getTypeName(), "实例部署者");
        nameMapping.put(TargetUserType.REPORTER.getTypeName(), "报告人");
        nameMapping.put(TargetUserType.SPECIFIER.getTypeName(), "指定用户");
        nameMapping.put(TargetUserType.HANDLER.getTypeName(), "操作者");
        nameMapping.put(TargetUserType.PROJECT_OWNER.getTypeName(), "项目所有者");
    }

}

