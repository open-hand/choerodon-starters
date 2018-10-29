package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import spock.lang.Specification

class LowercaseCountStrategySpec extends Specification {

    def lowercaseCountStrategy = new LowercaseCountStrategy()

    def "test validate"() {
        given: '创建PasswordPolicyMap和mapper'
        def passMap = new HashMap()
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when: 'no config'
        lowercaseCountStrategy.validate(passwordPolicyMap, null, '')
        then: '无异常'
        noExceptionThrown()

        when: '开启密码最小长度校验'
        passMap.put(PasswordPolicyType.LOWERCASE_COUNT.getValue(), 5)
        lowercaseCountStrategy.validate(passwordPolicyMap, null, 'abcdEF')
        then: '异常'
        def exe = thrown(CommonException)
        exe.code == 'error.password.policy.lowerCase'
    }

    def "test parse Config"() {
        when:
        def result = lowercaseCountStrategy.parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = lowercaseCountStrategy.getType()
        then:
        type == PasswordPolicyType.LOWERCASE_COUNT.getValue()
    }
}
