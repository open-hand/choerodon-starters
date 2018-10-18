package io.choerodon.swagger.exclude

import spock.lang.Specification
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule

class Swagger2HandDocumentationConfigurationSpec extends Specification {
    void setup() {
    }

    def "Swagger2Module"() {
        given: "参数准备"
        Swagger2HandDocumentationConfiguration swagger2HandDocumentationConfiguration = new Swagger2HandDocumentationConfiguration()
        when: "方法调用"
        def module = swagger2HandDocumentationConfiguration.swagger2Module()
        then: "结果比对"
        module instanceof Swagger2JacksonModule
    }
}
