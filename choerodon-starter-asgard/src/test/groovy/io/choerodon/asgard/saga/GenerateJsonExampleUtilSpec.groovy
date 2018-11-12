package io.choerodon.asgard.saga

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.asgard.preoperty.saga.Test
import spock.lang.Specification

class GenerateJsonExampleUtilSpec extends Specification {

    def '测试generate'() {
        given: '创建需要调用的objectMapper'
        def objectMapper = new ObjectMapper()

        when: '调用generate方法'
        String json = GenerateJsonExampleUtil.generate(Test, objectMapper, true)

        then: '验证结果'
        Test test = objectMapper.readValue(json, Test)
        test != null
        !test.test
        test.age == 0
        test.money == 0.0d
        test.username == 'string'
        test.inner.name == 'string'
        test.inner.id == 0l
    }
}
