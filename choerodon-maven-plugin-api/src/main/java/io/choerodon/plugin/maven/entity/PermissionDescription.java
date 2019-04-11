package io.choerodon.plugin.maven.entity;

import org.springframework.web.bind.annotation.RequestMethod;

public class PermissionDescription {
    private String path;
    private String service;
    private RequestMethod method;
    private PermissionEntity permission;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionDescription that = (PermissionDescription) o;

        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (method != that.method) return false;
        return permission != null ? permission.equals(that.permission) : that.permission == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (permission != null ? permission.hashCode() : 0);
        return result;
    }
}
