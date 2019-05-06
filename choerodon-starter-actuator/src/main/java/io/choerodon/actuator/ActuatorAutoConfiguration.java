package io.choerodon.actuator;

import io.choerodon.actuator.metadata.IMetadataDriver;
import io.choerodon.actuator.metadata.impl.BaseMetadataDriver;
import io.choerodon.actuator.metadata.impl.MSSQLMetadataDriver;
import io.choerodon.actuator.metadata.impl.MysqlMetadataDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@ComponentScan
public class ActuatorAutoConfiguration {
    @Bean
    public IMetadataDriver metadataDriver(DataSource source) throws SQLException {
        IMetadataDriver driver;
        try (Connection connect = source.getConnection()) {
            String database = connect.getMetaData().getDatabaseProductName();
            switch (database) {
                case "MySQL":
                    driver = new MysqlMetadataDriver(source);
                    break;
                case "Microsoft SQL Server":
                    driver = new MSSQLMetadataDriver(source);
                    break;
                default:
                    driver = new BaseMetadataDriver(source);
                    break;
            }
        }
        return driver;
    }
}
