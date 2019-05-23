package io.choerodon.actuator.dataset.domain;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseUpdateAction {
    private String sql;
    private List<Object> params;
    private int result;

    public void execute(DataSource dataSource) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                for (int i = 0; i < params.size(); i++){
                    ps.setObject(i + 1, params.get(i));
                }
                result = ps.executeUpdate();
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
