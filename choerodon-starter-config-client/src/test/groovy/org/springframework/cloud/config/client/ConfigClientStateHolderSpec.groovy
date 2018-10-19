package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigClientStateHolderSpec extends Specification {

    def "SetState"() {
        when: "方法调用"
        ConfigClientStateHolder.setState(newState)
        then: "无异常抛出"
        noExceptionThrown()
        where: "分支覆盖"
        newState << [null, "newState"]
    }
}
