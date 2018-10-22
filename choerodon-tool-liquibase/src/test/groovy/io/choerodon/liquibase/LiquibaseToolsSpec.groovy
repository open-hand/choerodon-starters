package io.choerodon.liquibase


import spock.lang.Specification
import spock.lang.Stepwise
/**
 *
 * @author zmf
 * @since 2018-10-18
 *
 */
@Stepwise
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LiquibaseTools.class)
class LiquibaseToolsSpec extends Specification {
//    @Autowired
//    ApplicationContext applicationContext

    LiquibaseExecutor liquibaseExecutor = Mock(LiquibaseExecutor)


    void setup() {
//        StartupRunner startupRunner = applicationContext.getBean(StartupRunner)
//        startupRunner.liquibaseExecutor = Mock(LiquibaseExecutor)
    }

    def main(String... values) {
        println("tttttttttttttttttttttttttttttt")
    }

    def "Main"() {
        when:
        LiquibaseTools.main()


        then: "校验结果"
        thrown(Exception)
        1 * liquibaseExecutor.execute() >> { true }
    }

    def "Startup runner 1"() {
        given: "配置环境"
        StartupRunner sr = new StartupRunner()
        sr.liquibaseExecutor = liquibaseExecutor


        when: "调用方法"
        sr.run()


        then: "结果分析"
        noExceptionThrown()

        1 * liquibaseExecutor.execute(_) >> { false }
//        when:
//        LiquibaseTools.main("")
//
//        then:
//        noExceptionThrown()
    }

    def "Startup runner 0"() {
        given: "配置环境"
        StartupRunner sr = new StartupRunner()
        sr.liquibaseExecutor = liquibaseExecutor


        when: "调用方法"
        sr.run()


        then: "结果分析"
        noExceptionThrown()

        1 * liquibaseExecutor.execute(_) >> { false }
    }
}
