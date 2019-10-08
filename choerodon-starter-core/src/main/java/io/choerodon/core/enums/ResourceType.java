package io.choerodon.core.enums;

/**
 * 资源类型，site/organization/project
 *
 * @author superlee
 * @since 2019-04-15
 */
public enum ResourceType {

    /**
     * site层
     */
    SITE("site"),
    /**
     * 组织层
     */
    ORGANIZATION("organization"),
    /**
     * 项目层
     */
    PROJECT("project");

    private String value;

    ResourceType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean contains(String value) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSite(String value) {
        return SITE.value.equals(value);
    }

    public static boolean isOrganization(String value) {
        return ORGANIZATION.value.equals(value);
    }

    public static boolean isProject(String value) {
        return PROJECT.value.equals(value);
    }
}
