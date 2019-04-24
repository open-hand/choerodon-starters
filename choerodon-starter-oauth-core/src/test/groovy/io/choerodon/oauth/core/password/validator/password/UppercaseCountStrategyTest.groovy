package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseUserDTO
import spock.lang.Specification

class UppercaseCountStrategyTest extends Specification {
    def uppercaseCountStrategy = new UppercaseCountStrategy()

    def "test validate"() {
        given:
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.UPPERCASE_COUNT.getValue(), 3)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        uppercaseCountStrategy.validate(passwordPolicyMap, new BaseUserDTO(), "abcdEF")

        then:
        def exe = thrown CommonException
        exe.code == 'error.password.policy.upperCase'
    }

    def "test parse Config"() {
        when:
        Object result = uppercaseCountStrategy.parseConfig("value")

        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = uppercaseCountStrategy.getType()
        then:
        type == PasswordPolicyType.UPPERCASE_COUNT.getValue()
    }
}