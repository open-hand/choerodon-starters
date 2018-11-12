package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import spock.lang.Specification

class MinLengthStrategySpec extends Specification {

    def minLengthStrategy = new MinLengthStrategy()

    def "test validate"() {
        given: '创建PasswordPolicyMap和mapper'
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.MIN_LENGTH.getValue(), 5)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        minLengthStrategy.validate(passwordPolicyMap, null, 'abc')

        then:
        def exe = thrown(CommonException)
        exe.code == 'error.password.policy.minLength'
    }


    def "test parse Config"() {
        when:
        def result = minLengthStrategy.parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = minLengthStrategy.getType()
        then:
        type == PasswordPolicyType.MIN_LENGTH.getValue()
    }
}
