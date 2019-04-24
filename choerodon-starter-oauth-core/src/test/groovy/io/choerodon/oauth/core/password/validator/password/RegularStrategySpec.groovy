package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseUserDTO
import spock.lang.Specification

class RegularStrategySpec extends Specification {
    RegularStrategy regularStrategy = new RegularStrategy()

    def "test validate"() {
        given:
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.REGULAR.getValue(), '[1-9]?\\d')
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        regularStrategy.validate(passwordPolicyMap, new BaseUserDTO(), "dfdfdf")

        then:
        def exe = thrown CommonException
        exe.code == 'error.password.policy.regular'
    }

    def "test parse Config"() {
        when:
        Object result = regularStrategy.parseConfig("value")

        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = regularStrategy.getType()
        then:
        type == PasswordPolicyType.REGULAR.getValue()
    }
}
