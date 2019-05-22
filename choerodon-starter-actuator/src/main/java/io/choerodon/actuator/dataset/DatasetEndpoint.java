package io.choerodon.actuator.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Endpoint(id = "dataset")
public class DatasetEndpoint {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Autowired
    private DataSource dataSource;
    @WriteOperation
    public List<Map<String, Object>> query(String action) throws SQLException, IOException {
        DatabaseQueryAction queryAction = OBJECT_MAPPER.readValue(action, DatabaseQueryAction.class);
        return queryAction.execute(dataSource);
    }
}
