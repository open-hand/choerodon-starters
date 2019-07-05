package io.choerodon.annotation.entity;

import java.util.Objects;

public class PermissionDescription {
    private String path;
    private String service;
    private String method;
    private String description;
    private PermissionEntity permission;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PermissionEntity getPermission() {
        return permission;
    }

    public void setPermission(PermissionEntity permission) {
        this.permission = permission;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionDescription that = (PermissionDescription) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(service, that.service) &&
                Objects.equals(method, that.method) &&
                Objects.equals(description, that.description) &&
                Objects.equals(permission, that.permission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, service, method, description, permission);
    }
}
