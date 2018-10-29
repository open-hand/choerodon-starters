package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import spock.lang.Specification

class DigitsCountStrategySpec extends Specification {

    def "test validate"() {
        given: '创建PasswordPolicyMap和mapper'
        def passMap = new HashMap()
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when: 'no config'
        new DigitsCountStrategy().validate(passwordPolicyMap, null, '')
        then: '无异常'
        noExceptionThrown()

        when: '开启密码最小长度校验'
        passMap.put(PasswordPolicyType.DIGITS_COUNT.getValue(), 10)
        new DigitsCountStrategy().validate(passwordPolicyMap, null, 'abcd')
        then: '异常'
        def exe = thrown(CommonException)
        exe.code == 'error.password.policy.digits'
    }

    def "test parse Config"() {
        when:
        def result = new DigitsCountStrategy().parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = new DigitsCountStrategy().getType()
        then:
        type == PasswordPolicyType.DIGITS_COUNT.getValue()
    }
}