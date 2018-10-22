package io.choerodon.liquibase


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
/**
 *
 * @author zmf
 * @since 2018-10-18
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(LiquibaseConfig.class)
class LiquibaseExecutorSpec extends Specification {
    @Autowired
    private LiquibaseExecutor liquibaseExecutor

//    @Autowired
//    private DataSource ds

    void setup() {
//        LiquibaseConfig config = new LiquibaseConfig()
//        config.dataSource = ds
//        liquibaseExecutor = config.getLiquibaseExecutor()
    }

    def "Execute"() {
//        given: "准备环境"
//        liquibaseExecutor = new LiquibaseExecutor(ds, new ProfileMap())

        when: "调用execute()方法"
        liquibaseExecutor.execute()

        then: "校验结果"
        noExceptionThrown()
    }
}
