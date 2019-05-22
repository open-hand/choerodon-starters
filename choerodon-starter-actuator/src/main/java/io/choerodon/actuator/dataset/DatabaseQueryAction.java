package io.choerodon.actuator.dataset;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DatabaseQueryAction {
    private String sql;
    private Object[] params;

    public List<Map<String, Object>> execute(DataSource dataSource) throws SQLException {
        List<Map<String, Object>> result = new LinkedList<>();
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                for (int i = 0; i < params.length; i++){
                    ps.setObject(i + 1, params[i]);
                }
                try(ResultSet results = ps.executeQuery()){
                    ResultSetMetaData metaData = results.getMetaData();
                    String[] columns = new String[metaData.getColumnCount()];
                    for (int i=0; i < columns.length; i++){
                        columns[i] = metaData.getColumnName(i + 1);
                    }
                    while (results.next()){
                        Map<String, Object> record = new TreeMap<>();
                        for (int i=0; i < columns.length; i++){
                            record.put(columns[i], results.getObject(i + 1));
                        }
                        result.add(record);
                    }
                }
            }
        }
        return result;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
