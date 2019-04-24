package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseUserDTO
import spock.lang.Specification

class NotUsernameStrategySpec extends Specification {

    NotUsernameStrategy notUsernameStrategy = new NotUsernameStrategy()

    def "test validate"() {
        given:
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.NOT_USERNAME.getValue(), true)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        notUsernameStrategy.validate(passwordPolicyMap, new BaseUserDTO(loginName: "abcd"), "abcd")

        then:
        def exe = thrown CommonException
        exe.code == 'error.password.policy.notUsername'
    }

    def "test parse Config"() {
        when:
        Object result = notUsernameStrategy.parseConfig("value")

        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = notUsernameStrategy.getType()
        then:
        type == PasswordPolicyType.NOT_USERNAME.getValue()
    }
}