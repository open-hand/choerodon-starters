package io.choerodon.liquibase.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.annotation.entity.PermissionDescription;
import io.choerodon.annotation.entity.PermissionEntity;
import io.choerodon.base.enums.ResourceType;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionLoader {
    private String serviceCode;
    private static final char UNDERLINE = '-';
    private Connection connection;
    private Map<String, Long> roleMap;

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public void execute(InputStream inputStream, Connection connection) throws IOException, SQLException {
        this.connection = connection;
        this.roleMap = queryRoleMap();
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
            permissionEntity.setType(ResourceType.SITE.value());
            permissionEntity.setPermissionWithin(true);
            description.setPermission(permissionEntity);
        }
        Long permissionKey = queryPermission(makeCode(description.getService(), resource, action));
        if (permissionKey != null){
            update(description.getPermission(), description.getPath(), description.getMethod(), description.getService(), resource, action);
        } else {
            insert(description.getPermission(), description.getPath(), description.getMethod(), description.getService(), resource, action);
            permissionKey = queryPermission(makeCode(description.getService(), resource, action));
            insertRolePermission(permissionKey, new HashSet<>(Arrays.asList(description.getPermission().getRoles())));
        }
    }

    private Map<String, Long> queryRoleMap() throws SQLException {
        Map<String, Long> result = new HashMap<>();
        try(PreparedStatement ps = connection.prepareStatement("SELECT ID, CODE FROM iam_role")){
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()){
                    result.put(resultSet.getString("CODE"), resultSet.getLong("ID"));
                }
            }
        }
        return result;
    }

    private void insertRolePermission(Long permissionKey, Set<String> roles) throws SQLException {
        roles.add("ADMIN");
        for (String roleCode : roles){
            Long roleId = roleMap.get(roleCode);
            if (roleId == null){
                System.out.println(roleMap);
                throw new IllegalArgumentException(String.format("role code [%s] not found.", roleCode));
            }
            try(PreparedStatement ps = connection.prepareStatement("INSERT INTO iam_role_permission (ROLE_ID, PERMISSION_ID) VALUES (?, ?)")) {
                ps.setLong(1, roleId);
                ps.setLong(2, permissionKey);
                if (ps.executeUpdate() != 1){
                    throw new IllegalStateException("update permission result not one.");
                }
            }
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
        try(PreparedStatement ps = connection.prepareStatement("UPDATE iam_permission SET PATH=?, SERVICE_CODE=?, CONTROLLER=?, ACTION=?" +
                ", METHOD=?, RESOURCE_LEVEL=?, IS_PUBLIC_ACCESS=?, IS_LOGIN_ACCESS=?, IS_WITHIN=? WHERE CODE=?")){
            ps.setString(1, path);
            ps.setString(2, service);
            ps.setString(3, resource);
            ps.setString(4, action);
            ps.setString(5, method);
            ps.setString(6, permission.getType());
            ps.setBoolean(7, permission.isPermissionPublic());
            ps.setBoolean(8, permission.isPermissionLogin());
            ps.setBoolean(9, permission.isPermissionWithin());
            ps.setString(10, makeCode(service, resource, action));
            if (ps.executeUpdate() != 1){
                throw new IllegalStateException("update permission result not one.");
            }
        }
    }

    private void insert(PermissionEntity permission, String path, String method, String service, String resource, String action) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement("INSERT INTO iam_permission (CODE, PATH, SERVICE_CODE, CONTROLLER, ACTION, METHOD," +
                " RESOURCE_LEVEL, IS_PUBLIC_ACCESS, IS_LOGIN_ACCESS, IS_WITHIN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
            ps.setString(1, makeCode(service, resource, action));
            ps.setString(2, path);
            ps.setString(3, service);
            ps.setString(4, resource);
            ps.setString(5, action);
            ps.setString(6, method);
            ps.setString(7, permission.getType());
            ps.setBoolean(8, permission.isPermissionPublic());
            ps.setBoolean(9, permission.isPermissionLogin());
            ps.setBoolean(10, permission.isPermissionWithin());
            if (ps.executeUpdate() != 1){
                throw new IllegalStateException("update permission result not one.");
            }
        }
    }

    private Long queryPermission(String code) throws SQLException {
        try(PreparedStatement ps = connection.prepareStatement("SELECT ID FROM iam_permission WHERE CODE=?")){
            ps.setString(1, code);
            try(ResultSet resultSet = ps.executeQuery()){
                if(resultSet.first()){
                    return resultSet.getLong("ID");
                }
                return null;
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
