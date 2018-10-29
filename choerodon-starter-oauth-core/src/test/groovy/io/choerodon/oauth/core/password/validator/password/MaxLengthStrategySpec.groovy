package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import spock.lang.Specification

class MaxLengthStrategySpec extends Specification {
    MaxLengthStrategy maxLengthStrategy = new MaxLengthStrategy()

    def "test validate"() {
        given: '创建PasswordPolicyMap和mapper'
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.MAX_LENGTH.getValue(), 10)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        maxLengthStrategy.validate(passwordPolicyMap, null, 'abcdabcdabcd')

        then:
        def exe = thrown(CommonException)
        exe.code == 'error.password.policy.maxLength'
    }


    def "test parse Config"() {
        when:
        def result = maxLengthStrategy.parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = maxLengthStrategy.getType()
        then:
        type == PasswordPolicyType.MAX_LENGTH.getValue()
    }
}