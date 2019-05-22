package io.choerodon.actuator.util

import io.choerodon.actuator.dataset.DatabaseInsertAction
import io.choerodon.actuator.dataset.DatabaseQueryAction
import io.choerodon.actuator.dataset.DatabaseUpdateAction
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
class ActuatorSpec extends Specification {
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
        MicroServiceInitData.processInitData(result.get("init-data"), connection, new TreeSet<>(["IAM_MENU_B", "IAM_PERMISSION"]))
        MicroServiceInitData.processInitData(result.get("init-data"), connection, new TreeSet<>(["IAM_MENU_B", "IAM_PERMISSION"]))
        connection.close()
        then:
        noExceptionThrown()
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM IAM_MENU_B", Integer.class) == 1
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM IAM_MENU_TL", Integer.class) == 2
    }

    def "Actuator Test" () {
        when:
        Map<String, Object> result1 = endpoint.query("permission")
        then:
        result1.size() > 0
        when:
        Map<String, Object> result2 = endpoint.query("all")
        then:
        result2.size() > 0
        when:
        Map<String, Object> result3 = endpoint.queryMetadata()
        then:
        result3.size() > 0
        when:
        Map<String, Object> result4 = endpoint.query("not match key")
        then:
        result4.size() == 0
    }

    def "Database INSERT Action Test" () {
        given:
        def action = new DatabaseInsertAction();
        action.sql = "INSERT INTO IAM_PERMISSION(CODE) VALUES (?)"
        action.params = ["test"]
        when:
        action.execute(dataSource)
        then:
        action.key != null
    }

    def "Database QUERY Action Test" () {
        given:
        def action = new DatabaseQueryAction();
        action.sql = "SELECT * FROM IAM_PERMISSION WHERE SERVICE_CODE=?"
        action.params = ["iam"]
        when:
        action.execute(dataSource)
        then:
        !action.result.isEmpty()
    }

    def "Database UPDATE Action Test" () {
        given:
        def action = new DatabaseUpdateAction();
        action.sql = "UPDATE IAM_PERMISSION SET SERVICE_CODE=? WHERE CODE=?"
        action.params = ["test-service", "test"]
        when:
        action.execute(dataSource)
        then:
        action.result == 1
    }

    def "Database DELETE Action Test" () {
        given:
        def action = new DatabaseUpdateAction();
        action.sql = "DELETE FROM IAM_PERMISSION WHERE SERVICE_CODE=?"
        action.params = ["iam"]
        when:
        action.execute(dataSource)
        then:
        action.result == 1
    }
}
