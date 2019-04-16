package io.choerodon.annotation.entity;

import io.choerodon.core.iam.ResourceLevel;

public class PermissionEntity {
    private String[] roles;
    private ResourceLevel level;
    private boolean permissionLogin;
    private boolean permissionPublic;
    private boolean permissionWithin;

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public ResourceLevel getLevel() {
        return level;
    }

    public void setLevel(ResourceLevel level) {
        this.level = level;
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
