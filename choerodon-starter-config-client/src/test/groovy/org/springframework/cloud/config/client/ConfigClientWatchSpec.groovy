package org.springframework.cloud.config.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigClientWatchSpec extends Specification {
    @Autowired
    public ConfigClientWatch configClientWatch

    def "WatchConfigServer"() {
        when: "方法调用"
        configClientWatch.watchConfigServer()
        then: "无异常抛出"
        noExceptionThrown()
    }
}
