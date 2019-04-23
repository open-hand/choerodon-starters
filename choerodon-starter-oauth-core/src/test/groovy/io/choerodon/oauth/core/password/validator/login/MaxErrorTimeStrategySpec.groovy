package io.choerodon.oauth.core.password.validator.login

import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO
import io.choerodon.oauth.core.password.domain.BaseUserDTO
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper
import spock.lang.Specification

class MaxErrorTimeStrategySpec extends Specification {

    def "test validate"() {
        given: '创建PasswordPolicyMap和mapper'
        def loginMap = new HashMap()
        def passwordPolicyMap = new PasswordPolicyMap(null, loginMap, true, true)
        loginMap.put('maxErrorTime', 10)
        loginMap.put('enableLock', true)

        def baseLoginAttemptTimesMapper = Mock(BaseLoginAttemptTimesMapper) {
            def baseLoginAttemptTimesDO = new BaseLoginAttemptTimesDTO()
            baseLoginAttemptTimesDO.setLoginAttemptTimes(11)
            selectOne(_) >> baseLoginAttemptTimesDO
        }

        when: '开启最大输错次数限制'
        def result = new MaxErrorTimeStrategy(baseLoginAttemptTimesMapper).validate(passwordPolicyMap, new BaseUserDTO(), '')
        then:
        !result

        when: '不开启最大输错次数限制'
        loginMap.put('enableLock', false)
        def result1 = new MaxErrorTimeStrategy(baseLoginAttemptTimesMapper).validate(passwordPolicyMap, new BaseUserDTO(), '')
        then:
        result1
    }

    def "test parse Config"() {
        when:
        def result = new MaxErrorTimeStrategy(null).parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = new MaxErrorTimeStrategy(null).getType()
        then:
        type == PasswordPolicyType.MAX_ERROR_TIME.getValue()
    }
}