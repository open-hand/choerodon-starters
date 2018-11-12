package io.choerodon.config.builder

import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class YamlBuilderSpec extends Specification {
    def "Build"() {
        given: "构造请求参数"
        YamlBuilder builder = new YamlBuilder()
        Map<String, Object> map = new HashMap<>()
        map.put("id", 1)
        map.put("name", "name")
        map.put("description", null)

        when: "调用方法"
        builder.build(map)

        then: "校验结果"
        noExceptionThrown()
    }
}
