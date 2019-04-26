package io.choerodon.annotation.entity;

public class PermissionEntity {
    private String[] roles = new String[0];
    private String type;
    private boolean permissionLogin;
    private boolean permissionPublic;
    private boolean permissionWithin;

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isPermissionWithin() {
        return permissionWithin;
    }

    public void setPermissionWithin(boolean permissionWithin) {
        this.permissionWithin = permissionWithin;
    }
}
