package io.choerodon.core.enums;

/**
 * 项目分类，agile/program/analytical
 *
 * @author superlee
 * @since 2019-04-15
 */
public enum ProjectCategory {

    /**
     * agile
     */
    AGILE("agile"),
    /**
     * program
     */
    PROGRAM("program"),
    /**
     * analytical
     */
    ANALYTICAL("analytical");

    private String value;

    ProjectCategory(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean contains(String value) {
        for (ProjectCategory projectCategory : ProjectCategory.values()) {
            if (projectCategory.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAgile(String value) {
        return AGILE.value.equals(value);
    }

    public static boolean isProgram(String value) {
        return PROGRAM.value.equals(value);
    }

    public static boolean isAnalytical(String value) {
        return ANALYTICAL.value.equals(value);
    }
}
