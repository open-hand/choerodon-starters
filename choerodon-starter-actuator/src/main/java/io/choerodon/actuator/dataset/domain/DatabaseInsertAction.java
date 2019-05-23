package io.choerodon.actuator.dataset.domain;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseInsertAction {
    private String sql;
    private List<Object> params;
    private Object key;

    public void execute(DataSource dataSource) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                for (int i = 0; i < params.size(); i++){
                    ps.setObject(i + 1, params.get(i));
                }
                ps.executeUpdate();
                try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                    generatedKeys.next();
                    key = generatedKeys.getObject(1);
                }
            }
        }
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }
}