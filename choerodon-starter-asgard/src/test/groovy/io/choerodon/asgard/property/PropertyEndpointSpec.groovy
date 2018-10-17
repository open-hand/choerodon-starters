package io.choerodon.asgard.property

import io.choerodon.asgard.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PropertyEndpointSpec  extends Specification {

    def '测试propertyData方法'() {
        given: '创建对象'
        def propertyData = new PropertyData()
        def propertyEndpoint = new PropertyEndpoint(propertyData)

        when: '调用propertyData方法'
        def res = propertyEndpoint.propertyData()

        then: '验证结果'
        res == propertyData
    }

}
