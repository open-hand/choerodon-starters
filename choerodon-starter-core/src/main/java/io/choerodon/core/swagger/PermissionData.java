package io.choerodon.core.swagger;

/**
 * @author flyleft
 * 2018/4/13
 */
public class PermissionData {

    private String action;

    private String menuLevel;

    private String permissionLevel;

    private String[] roles;

    private boolean permissionLogin;

    private boolean permissionPublic;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(String menuLevel) {
        this.menuLevel = menuLevel;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public boolean isPermissionLogin() {
        return permissionLogin;
    }

    public void setPermissionLogin(boolean permissionLogin) {
        this.permissionLogin = permissionLogin;
    }

    public boolean isPermissionPublic() {
        return permissionPublic;
    }

    public void setPermissionPublic(boolean permissionPublic) {
        this.permissionPublic = permissionPublic;
    }

}
