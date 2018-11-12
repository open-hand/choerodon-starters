package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RetryConfigurationSpec extends Specification {
    def "ConfigServerRetryInterceptor"() {
        given: "参数准备"
        def properties = new RetryProperties()
        properties.setInitialInterval(1000)
        properties.setMaxAttempts(6)
        properties.setMaxInterval(2000)
        properties.setMultiplier(1.1)
        when: "调用方法"
        new ConfigServiceBootstrapConfiguration.RetryConfiguration().configServerRetryInterceptor(properties)
        then: "无异常抛出"
        noExceptionThrown()
    }
}
