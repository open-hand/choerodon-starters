package io.choerodon.actuator.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

public class MicroServiceInitData {
    /**
     * 将微服务初始化数据的Json执行到数据库
     *
     * @param data       actuator json 的 init-data 块
     * @param connection 数据库连接
     */
    public static void processInitData(JsonNode data, Connection connection, Set<String> tables) throws SQLException {
        Iterator<String> tableNames = data.fieldNames();
        while (tableNames.hasNext()) {
            String tableName = tableNames.next();
            if(tables.contains(tableName)){
                processTableData(data.get(tableName), tableName, connection);
            }
        }
    }

    private static void processTableData(JsonNode data, String tableName, Connection connection) throws SQLException {
        if (data.size() == 0) {
            return;
        }
        TableDescription description = makeTableDescription(data.get(0), tableName);
        for (int i = 0; i < data.size(); i++) {
            processRowData(data.get(i), tableName, connection, description);
        }
    }

    private static void processRowData(JsonNode data, String tableName, Connection connection, TableDescription description) throws SQLException {
        Object keyData = queryPrimaryKeyByUnique(data, tableName, connection, description);
        if (keyData == null){
            insertRowData(data, tableName, connection, description);
            keyData = queryPrimaryKeyByUnique(data, tableName, connection, description);
        } else {
            updateRowData(data, tableName, connection, description, keyData);
        }
        if (!description.multiLanguageColumns.isEmpty()){
            processRowMultiLanguage(data, tableName, connection, description, keyData);
        }
    }

    private static void processRowMultiLanguage(JsonNode data, String tableName, Connection connection, TableDescription description, Object keyData) throws SQLException {
        String upperCaseTableName = tableName.toUpperCase();
        String multiLanguageTableName = upperCaseTableName + "_TL";
        if (upperCaseTableName.endsWith("_B")){
            multiLanguageTableName = upperCaseTableName.substring(0, upperCaseTableName.length() - 2) + "_TL";
        }
        for (String language:description.multiLanguages){
            if (checkExitsMultiLanguageRecord(data, multiLanguageTableName, connection, description, keyData, language)){
                updateRowMultiLanguage(data, multiLanguageTableName, connection, description, keyData, language);
            } else {
                insertRowMultiLanguage(data, multiLanguageTableName, connection, description, keyData, language);
            }
        }
    }

    private static void updateRowMultiLanguage(JsonNode data, String tableName, Connection connection, TableDescription description, Object keyData, String language) throws SQLException {
        List<JsonNode> updateParameters = new ArrayList<>();
        StringJoiner updateColumnsJoiner = new StringJoiner(",");
        for (String column: description.multiLanguageColumns){
            updateColumnsJoiner.add(column + "=?");
            updateParameters.add(data.get(column + ":" + language));
        }
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        sql.append(updateColumnsJoiner.toString());
        sql.append(" WHERE ");
        sql.append(description.primaryKey);
        sql.append("=? AND LANG=?");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            for (int updateParameterIndex = 0; updateParameterIndex < updateParameters.size(); updateParameterIndex++){
                setNodeValue(updateParameters.get(updateParameterIndex), statement, updateParameterIndex + 1);
            }
            statement.setObject(updateParameters.size() + 1, keyData);
            statement.setString(updateParameters.size() + 2, language);
            if(statement.executeUpdate() != 1){
                throw new IllegalStateException("Execute update result not one.");
            }
        }
    }

    private static void insertRowMultiLanguage(JsonNode data, String tableName, Connection connection, TableDescription description, Object keyData, String language) throws SQLException {
        List<JsonNode> insertParameters = new ArrayList<>();
        StringJoiner insertColumnsJoiner = new StringJoiner(",");
        StringJoiner insertParametersJoiner = new StringJoiner(",");
        for (String column: description.multiLanguageColumns){
            insertColumnsJoiner.add(column);
            insertParametersJoiner.add("?");
            insertParameters.add(data.get(column + ":" + language));
        }
        insertColumnsJoiner.add(description.primaryKey);
        insertParametersJoiner.add("?");
        insertColumnsJoiner.add("LANG");
        insertParametersJoiner.add("?");
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" ( ");
        sql.append(insertColumnsJoiner.toString());
        sql.append(" ) VALUES ( ");
        sql.append(insertParametersJoiner.toString());
        sql.append(" )");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            for (int insertParameterIndex = 0; insertParameterIndex < insertParameters.size(); insertParameterIndex++){
                setNodeValue(insertParameters.get(insertParameterIndex), statement, insertParameterIndex + 1);
            }
            statement.setObject(insertParameters.size() + 1, keyData);
            statement.setString(insertParameters.size() + 2, language);
            if(statement.executeUpdate() != 1){
                throw new IllegalStateException("Execute update result not one.");
            }
        }
    }

    private static boolean checkExitsMultiLanguageRecord(JsonNode data, String tableName, Connection connection, TableDescription description, Object keyData, String language) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        sql.append(description.primaryKey);
        sql.append("=? AND LANG=?");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            statement.setObject(1, keyData);
            statement.setString(2, language);
            try(ResultSet resultSet = statement.executeQuery()){
                resultSet.first();
                return resultSet.getInt("COUNT(*)") > 0;
            }
        }
    }

    private static void updateRowData(JsonNode data, String tableName, Connection connection, TableDescription description, Object keyData) throws SQLException {
        List<JsonNode> updateParameters = new ArrayList<>();
        StringJoiner updateColumnsJoiner = new StringJoiner(",");
        for (String uniqueKey: description.uniqueKeys){
            updateColumnsJoiner.add(uniqueKey + "=?");
            updateParameters.add(data.get("#" + uniqueKey));
        }
        for (String column: description.updateColumns){
            updateColumnsJoiner.add(column + "=?");
            updateParameters.add(data.get(column));
        }
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        sql.append(updateColumnsJoiner.toString());
        sql.append(" WHERE ");
        sql.append(description.primaryKey);
        sql.append("=?");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            for (int updateParameterIndex = 0; updateParameterIndex < updateParameters.size(); updateParameterIndex++){
                setNodeValue(updateParameters.get(updateParameterIndex), statement, updateParameterIndex + 1);
            }
            statement.setObject(updateParameters.size() + 1, keyData);
            if(statement.executeUpdate() != 1){
                throw new IllegalStateException("Execute update result not one.");
            }
        }
    }

    private static void insertRowData(JsonNode data, String tableName, Connection connection, TableDescription description) throws SQLException {
        List<JsonNode> insertParameters = new ArrayList<>();
        StringJoiner insertColumnsJoiner = new StringJoiner(",");
        StringJoiner insertParametersJoiner = new StringJoiner(",");
        for (String uniqueKey: description.uniqueKeys){
            insertColumnsJoiner.add(uniqueKey);
            insertParametersJoiner.add("?");
            insertParameters.add(data.get("#" + uniqueKey));
        }
        for (String column: description.insertColumns){
            insertColumnsJoiner.add(column);
            insertParametersJoiner.add("?");
            insertParameters.add(data.get(column));
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(tableName);
        sql.append(" ( ");
        sql.append(insertColumnsJoiner.toString());
        sql.append(" ) VALUES ( ");
        sql.append(insertParametersJoiner.toString());
        sql.append(" )");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            for (int insertParameterIndex = 0; insertParameterIndex < insertParameters.size(); insertParameterIndex++){
                setNodeValue(insertParameters.get(insertParameterIndex), statement, insertParameterIndex + 1);
            }
            if(statement.executeUpdate() != 1){
                throw new IllegalStateException("Execute update result not one.");
            }
        }
    }

    private static Object queryPrimaryKeyByUnique(JsonNode data, String tableName, Connection connection, TableDescription description) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(description.primaryKey);
        sql.append(" FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        for (String uniqueKey : description.uniqueKeys) {
            sql.append(uniqueKey);
            sql.append("=? AND ");
        }
        sql.append("1=1");
        try(PreparedStatement statement = connection.prepareStatement(sql.toString())){
            int uniqueKeyIndex = 0;
            for (String uniqueKey : description.uniqueKeys) {
                uniqueKeyIndex++;
                setNodeValue(data.get("#" + uniqueKey), statement, uniqueKeyIndex);
            }
            try(ResultSet resultSet = statement.executeQuery()){
                if (!resultSet.first()){
                    return null;
                }
                return resultSet.getObject(description.primaryKey);
            }
        }
    }

    private static void setNodeValue(JsonNode data, PreparedStatement statement, int i) throws SQLException {
        switch (data.getNodeType()) {
            case NUMBER:
                statement.setLong(i, data.longValue());
                break;
            case BOOLEAN:
                statement.setBoolean(i, data.booleanValue());
                break;
            case NULL:
                statement.setObject(i, null);
                break;
            default:
                statement.setString(i, data.asText());
                break;
        }
    }

    private static TableDescription makeTableDescription(JsonNode header, String tableName) {
        TableDescription description = new TableDescription();
        Iterator<String> columnNames = header.fieldNames();
        while (columnNames.hasNext()) {
            String columnName = columnNames.next();
            if (columnName.startsWith("*")) {
                if (description.primaryKey != null) {
                    throw new IllegalStateException("Multi-primary key not supported in table: " + tableName);
                }
                description.primaryKey = columnName.substring(1);
                continue;
            }
            if (columnName.startsWith("#")) {
                description.uniqueKeys.add(columnName.substring(1));
                continue;
            }
            if (columnName.contains(":")) {
                String[] columnSplit = columnName.split(":");
                if (columnSplit.length != 2) {
                    throw new IllegalStateException("Multi-languages column format error in table: " + tableName);
                }
                description.multiLanguages.add(columnSplit[1]);
                description.multiLanguageColumns.add(columnSplit[0]);
                continue;
            }
            if (columnName.startsWith("@")) {
                description.insertColumns.add(columnName.substring(1));
            } else {
                description.updateColumns.add(columnName);
                description.insertColumns.add(columnName);
            }
        }
        if (description.primaryKey == null) {
            throw new IllegalStateException("Must have a primary key in table: " + tableName);
        }
        if (description.uniqueKeys.isEmpty()) {
            throw new IllegalStateException("Must have at least one unique key in table: " + tableName);
        }
        return description;
    }

    private static class TableDescription {
        String primaryKey = null;
        Set<String> uniqueKeys = new TreeSet<>();
        Set<String> insertColumns = new TreeSet<>();
        Set<String> updateColumns = new TreeSet<>();
        Set<String> multiLanguageColumns = new TreeSet<>();
        Set<String> multiLanguages = new TreeSet<>();
    }

}
