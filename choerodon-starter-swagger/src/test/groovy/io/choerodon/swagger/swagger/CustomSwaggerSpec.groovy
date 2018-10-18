package io.choerodon.swagger.swagger

import io.choerodon.swagger.swagger.extra.ExtraData
import spock.lang.Specification

class CustomSwaggerSpec extends Specification {


    def "SetExtraData"() {
        when: "set"
        CustomSwagger customSwagger = new CustomSwagger()
        def mock = Mock(ExtraData)
        customSwagger.setExtraData(mock)
        customSwagger.hashCode()
        then: "get"
        customSwagger.getExtraData().equals(mock)
    }

    def "Equals"() {
        given: "参数准备"
        CustomSwagger customSwagger1 = new CustomSwagger()
        CustomSwagger customSwagger2 = new CustomSwagger()
        when: "equals"
        def equals1 = customSwagger1.equals(customSwagger1)
        def equals2 = customSwagger1.equals(null)
        def equals3 = customSwagger1.equals(customSwagger2)
        then: "result"
        equals1 == true
        equals2 == false
        equals3 == true
    }
}
