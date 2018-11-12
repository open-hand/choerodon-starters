package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigClientAutoConfigurationSpec extends Specification {
    def "ConfigClientProperties"() {
        given: "参数准备"
        def environment = Mock(Environment)
        environment.getActiveProfiles() >> { return new String[0] }
        def context = Mock(ApplicationContext)
        when: "方法调用"
        def properties = new ConfigClientAutoConfiguration().configClientProperties(environment, context)
        then: "结果比对"
        noExceptionThrown()
        properties instanceof ConfigClientProperties
    }

    def "ConfigClientWatch"() {
        when: "方法调用"
        def watch = new ConfigClientAutoConfiguration.ConfigClientWatchConfiguration().configClientWatch(Mock(ContextRefresher))
        then: "结果比对"
        noExceptionThrown()
        watch instanceof ConfigClientWatch
    }
}
