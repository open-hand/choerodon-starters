package io.choerodon.liquibase

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import java.lang.reflect.Field

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

    void cleanup() {
        File file = new File("temp/")
        file.deleteDir()
    }

    def "Execute"() {
        when: "调用execute()方法"
        liquibaseExecutor.execute()

        then: "校验结果"
        noExceptionThrown()
    }

    def "Execute twice"() {
        when: "调用execute()方法"
        liquibaseExecutor.execute()
        liquibaseExecutor.execute()

        then: "校验结果"
        noExceptionThrown()
    }

    def "Execute initialization in jar file"() {
        given: "准备上下文"
        // 设置 jar 包位置
        liquibaseExecutor.defaultJar = liquibaseExecutor.class.getClassLoader().getResource("resource-jar.jar").getFile()
        // 设置jar包中的搜索路径
        Field dir = liquibaseExecutor.getClass().getDeclaredField("defaultDir")
        dir.setAccessible(true)
        dir.set(liquibaseExecutor, "script/db")

        when: "调用execute()方法"
        liquibaseExecutor.execute()

        then: "校验结果"
        noExceptionThrown()
    }
}
