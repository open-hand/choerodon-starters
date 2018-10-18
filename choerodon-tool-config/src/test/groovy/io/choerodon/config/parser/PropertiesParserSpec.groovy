package io.choerodon.config.parser

import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class PropertiesParserSpec extends Specification {
    private PropertiesParser propertiesParser = new PropertiesParser()

    def "Parse"() {
        given: "构造请求参数"
        def file = new File(this.class.getResource('/application.properties').toURI())

        when: "调用方法"
        Map<String, Object> map = propertiesParser.parse(file)

        then: "校验结果"
        map.get("gateway.names").equals("api-gateway, gateway-helper")
    }
}
