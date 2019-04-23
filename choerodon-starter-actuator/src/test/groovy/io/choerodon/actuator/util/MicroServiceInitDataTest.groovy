package io.choerodon.actuator.util

import io.choerodon.actuator.endpoint.ActuatorEndpoint
import io.choerodon.actuator.endpoint.TestApplication
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

import javax.annotation.PostConstruct
import javax.sql.DataSource
import java.sql.Connection

@Import([LiquibaseConfig])
@SpringBootTest(classes = TestApplication, properties = "spring.application.name=test-app")
class MicroServiceInitDataTest extends Specification {
    @Autowired
    LiquibaseExecutor liquibaseExecutor;
    @Autowired
    private ActuatorEndpoint endpoint
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostConstruct
    void init() {
        //通过liquibase初始化h2数据库
        liquibaseExecutor.execute()
    }
    def "Micro Service Init Data Test" () {
        when:
        Map<String, Object> result = endpoint.query("init-data")
        Connection connection = dataSource.getConnection();
        MicroServiceInitData.processInitData(result.get("init-data"), connection)
        MicroServiceInitData.processInitData(result.get("init-data"), connection)
        connection.close()
        then:
        noExceptionThrown()
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM IAM_MENU_B", Integer.class) == 1
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM IAM_MENU_TL", Integer.class) == 2
    }
}
