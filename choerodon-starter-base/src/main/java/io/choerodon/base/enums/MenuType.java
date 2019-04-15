package io.choerodon.base.enums;

/**
 * 资源类型，site/organization/project
 *
 * @author superlee
 * @since 2019-04-15
 */
public enum MenuType {

    /**
     * 顶层菜单
     */
    TOP("top"),
    /**
     * 菜单
     */
    MENU("menu"),
    /**
     * 菜单项
     */
    MENU_ITEM("menu_item");

    private String value;

    MenuType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean contains(String value) {
        for (MenuType menuType : MenuType.values()) {
            if (menuType.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTop(String value) {
        return TOP.value.equals(value);
    }

    public static boolean isMenu(String value) {
        return MENU.value.equals(value);
    }

    public static boolean isMenuItem(String value) {
        return MENU_ITEM.value.equals(value);
    }

}
