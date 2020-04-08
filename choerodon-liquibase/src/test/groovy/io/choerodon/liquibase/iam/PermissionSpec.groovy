package io.choerodon.liquibase.iam

import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.liquibase.TestLiquibaseApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

@Import(LiquibaseConfig.class)
@SpringBootTest(classes = TestLiquibaseApplication, properties = "data.drop=false")
class PermissionSpec extends Specification {
    @Autowired
    private LiquibaseExecutor liquibaseExecutor
    @Autowired
    private DataSource dataSource;

    def "Permission Load"() {
        when:
        if (!liquibaseExecutor.execute()) {
            throw new Exception("liquibase failed.")
        }
        //执行两次测试更新
        if (!liquibaseExecutor.execute()) {
            throw new Exception("liquibase failed.")
        }
        then:
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet1 = statement.executeQuery("SELECT COUNT(*) FROM iam_permission")
        resultSet1.next()
        resultSet1.getInt("COUNT(*)") > 0
        resultSet1.close()
        ResultSet resultSet2 = statement.executeQuery("SELECT SERVICE_CODE, IS_WITHIN FROM iam_permission WHERE CODE = 'iam-service.user.listUsersByIds'")
        resultSet2.next()
        resultSet2.getString("SERVICE_CODE") == "iam-service"
        resultSet2.getBoolean("IS_WITHIN")
        resultSet2.close()
        ResultSet resultSet3 = statement.executeQuery("SELECT SERVICE_CODE, IS_WITHIN FROM iam_permission WHERE CODE = 'iam-service.user.query'")
        resultSet3.next()
        resultSet3.getString("SERVICE_CODE") == "iam-service"
        !resultSet3.getBoolean("IS_WITHIN")
        resultSet3.close()
        statement.close()
        connection.close()
        noExceptionThrown()
    }
}
