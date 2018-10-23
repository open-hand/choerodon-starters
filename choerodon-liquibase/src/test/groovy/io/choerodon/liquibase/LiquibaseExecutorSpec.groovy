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
@SpringBootTest(classes = TestLiquibaseApplication)
@Import(LiquibaseConfig.class)
class LiquibaseExecutorSpec extends Specification {
    @Autowired
    private LiquibaseExecutor liquibaseExecutor

    def "Execute"() {
        when: "调用execute()方法"
        liquibaseExecutor.execute()

        then: "校验结果"
        noExceptionThrown()
    }
}
