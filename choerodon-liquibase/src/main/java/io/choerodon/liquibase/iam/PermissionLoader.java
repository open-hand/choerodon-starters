package io.choerodon.liquibase.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.plugin.maven.entity.PermissionDescription;
import io.choerodon.plugin.maven.entity.PermissionEntity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PermissionLoader {
    private String serviceCode;
    private static final char UNDERLINE = '-';
    private Connection connection;

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public void execute(InputStream inputStream, Connection connection) throws IOException, SQLException {
        this.connection = connection;
        ObjectMapper objectMapper = new ObjectMapper();
        Map <String, PermissionDescription> descriptions = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, PermissionDescription.class));
        for (Map.Entry<String, PermissionDescription> entry : descriptions.entrySet()){
            entry.getValue().setService(serviceCode);
            processDescription(entry.getKey(), entry.getValue());
        }
    }
    private void processDescription(String key, PermissionDescription description) throws SQLException {
        String[] names = key.split("\\.");
        String controllerClassName = names[names.length - 2];
        String action = names[names.length - 1];
        String resource = camelToUnderline(controllerClassName).replace("-controller", "");

        if (description.getPermission() == null){
            PermissionEntity permissionEntity = new PermissionEntity();
            permissionEntity.setLevel(ResourceLevel.PROJECT);
            permissionEntity.setPermissionWithin(true);
            description.setPermission(permissionEntity);
        }
        if (checkExists(makeCode(description.getService(), resource, action))){
            update(description.getPermission(), description.getPath(), description.getMethod().name(), description.getService(), resource, action);
        } else {
            insert(description.getPermission(), description.getPath(), description.getMethod().name(), description.getService(), resource, action);
        }
    }

    private String makeCode(String service, String resource, String action){
        String code = resource + "." + action;
        if (service != null){
            code = service + "." + code;
        }
        return code;
    }

    private void update(PermissionEntity permission, String path, String method, String service, String resource, String action) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement("UPDATE iam_permission SET PATH=?, SERVICE_CODE=?, FD_RESOURCE=?, ACTION=?" +
                ", METHOD=?, RESOURCE_LEVEL=?, IS_PUBLIC_ACCESS=?, IS_LOGIN_ACCESS=?, WITHIN=? WHERE CODE=?")){
            ps.setString(1, path);
            ps.setString(2, service);
            ps.setString(3, resource);
            ps.setString(4, action);
            ps.setString(5, method);
            ps.setString(6, permission.getLevel().toString());
            ps.setBoolean(7, permission.isPermissionPublic());
            ps.setBoolean(8, permission.isPermissionLogin());
            ps.setBoolean(9, permission.isPermissionWithin());
            ps.setString(10, makeCode(service, resource, action));
            if (ps.executeUpdate() != 1){
                throw new RuntimeException("update permission result not one.");
            }
        }
    }

    private void insert(PermissionEntity permission, String path, String method, String service, String resource, String action) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement("INSERT INTO iam_permission (CODE, PATH, SERVICE_CODE, FD_RESOURCE, ACTION, METHOD," +
                " RESOURCE_LEVEL, IS_PUBLIC_ACCESS, IS_LOGIN_ACCESS, WITHIN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            ps.setString(1, makeCode(service, resource, action));
            ps.setString(2, path);
            ps.setString(3, service);
            ps.setString(4, resource);
            ps.setString(5, action);
            ps.setString(6, method);
            ps.setString(7, permission.getLevel().toString());
            ps.setBoolean(8, permission.isPermissionPublic());
            ps.setBoolean(9, permission.isPermissionLogin());
            ps.setBoolean(10, permission.isPermissionWithin());
            if (ps.executeUpdate() != 1){
                throw new RuntimeException("update permission result not one.");
            }
        }
    }

    private boolean checkExists(String code) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM iam_permission WHERE CODE=?")){
            ps.setString(1, code);
            try(ResultSet resultSet = ps.executeQuery()){
                resultSet.first();
                return resultSet.getInt("COUNT(*)") > 0;
            }
        }
    }

    /**
     * 驼峰格式字符串转换为下划线格式字符串
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0){
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
