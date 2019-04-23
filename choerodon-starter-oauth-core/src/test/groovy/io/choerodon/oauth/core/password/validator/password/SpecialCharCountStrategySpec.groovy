package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseUserDTO
import spock.lang.Specification

class SpecialCharCountStrategySpec extends Specification {
    SpecialCharCountStrategy specialCharCountStrategy = new SpecialCharCountStrategy()

    def "test validate"() {
        given:
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.SPECIALCHAR_COUNT.getValue(), 10)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        specialCharCountStrategy.validate(passwordPolicyMap, new BaseUserDTO(), "1@2")

        then:
        def exe = thrown CommonException
        exe.code == 'error.password.policy.specialChar'
    }

    def "test parse Config"() {
        when:
        Object result = specialCharCountStrategy.parseConfig("value")

        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = specialCharCountStrategy.getType()
        then:
        type == PasswordPolicyType.SPECIALCHAR_COUNT.getValue()
    }
}