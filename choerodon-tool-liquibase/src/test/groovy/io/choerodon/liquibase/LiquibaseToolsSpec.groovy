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
class LiquibaseToolsSpec extends Specification {
//    LiquibaseExecutor liquibaseExecutor = Mock(LiquibaseExecutor)


//    def main(String... values) {
//        println("tttttttttttttttttttttttttttttt")
//    }

    def "Main"() {
        when:
        LiquibaseTools.main()

        then: "校验结果"
        noExceptionThrown()
    }

//    def "Startup runner 1"() {
//        given: "配置环境"
//        StartupRunner sr = new StartupRunner()
//        sr.liquibaseExecutor = liquibaseExecutor
//        liquibaseExecutor.execute() >> { false }
//
//        when: "调用方法"
//        sr.run()
//
//        then: "结果分析"
//        noExceptionThrown()
//    }
//
//    def "Startup runner 0"() {
//        given: "配置环境"
//        StartupRunner sr = new StartupRunner()
//        sr.liquibaseExecutor = liquibaseExecutor
//        liquibaseExecutor.execute() >> { false }
//
//        when: "调用方法"
//        sr.run()
//
//        then: "结果分析"
//        noExceptionThrown()
//    }
}
