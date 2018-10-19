package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigClientHealthPropertiesSpec extends Specification {
    def "ConfigClientHealthProperties"() {
        given: "构造"
        def properties = new ConfigClientHealthProperties()
        when: "set"
        properties.setEnabled(false)
        properties.setTimeToLive(300000L)
        then: "get"
        !properties.isEnabled()
        properties.getTimeToLive().equals(300000L)
    }
}
