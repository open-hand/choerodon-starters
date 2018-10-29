package io.choerodon.oauth.core.password.validator.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.PasswordPolicyMap
import io.choerodon.oauth.core.password.PasswordPolicyType
import io.choerodon.oauth.core.password.domain.BaseUserDO
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class NotRecentStrategySpec extends Specification {

    def "test validate"() {
        given: 'mock mapper'
        def encode = new BCryptPasswordEncoder()
        def mapper = Mock(BasePasswordHistoryMapper) {
            selectPasswordByUser(_, _) >> [encode.encode('abcd')]
        }
        def passMap = new HashMap()
        passMap.put(PasswordPolicyType.NOT_RECENT.getValue(), 5)
        def passwordPolicyMap = new PasswordPolicyMap(passMap, null, true, true)

        when:
        Object result = new NotRecentStrategy(mapper).validate(passwordPolicyMap, new BaseUserDO(), 'abcd')

        then:
        result == null
        def exe = thrown CommonException
        exe.code.contains('error.password.policy.notRecent')
    }

    def "test parse Config"() {
        when:
        def result = new NotRecentStrategy(null).parseConfig(null)
        then:
        result == null
    }

    def "test getType"() {
        when:
        def type = new NotRecentStrategy(null).getType()
        then:
        type == PasswordPolicyType.NOT_RECENT.getValue()
    }

}