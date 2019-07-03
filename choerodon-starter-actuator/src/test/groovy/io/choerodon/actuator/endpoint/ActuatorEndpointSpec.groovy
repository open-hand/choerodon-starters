package io.choerodon.actuator.endpoint

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = TestApplication, properties = "spring.application.name=test-app")
class ActuatorEndpointSpec extends Specification {
    @Autowired
    private ActuatorEndpoint endpoint

    def "Actuator Test"() {
        when:
        Map<String, Object> result1 = endpoint.query("permission")
        then:
        result1.size() > 0
        when:
        Map<String, Object> result2 = endpoint.query("all")
        then:
        result2.size() > 0
        when:
        Map<String, Object> result4 = endpoint.query("not match key")
        then:
        result4.size() == 0
    }
}
