package io.choerodon.oauth.core.password

import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDTO
import spock.lang.Specification

class PasswordPolicyMapSpec extends Specification {

    def passwordPolicyMap = new PasswordPolicyMap(new HashMap<String, Object>(), new HashMap<String, Object>(), true, true)

    def "test get Password Policies"() {
        when:
        Set<String> result = passwordPolicyMap.getPasswordPolicies()

        then:
        result != null
    }

    def "test get Login Policies"() {
        when:
        Set<String> result = passwordPolicyMap.getLoginPolicies()

        then:
        result != null
    }

    def "test is Enable Password"() {
        when:
        Boolean result = passwordPolicyMap.isEnablePassword()

        then:
        result == Boolean.TRUE
    }

    def "test is Enable Security"() {
        when:
        Boolean result = passwordPolicyMap.isEnableSecurity()

        then:
        result == Boolean.TRUE
    }

    def "test parse"() {
        given: '创建BasePasswordPolicyDO'
        def pdo = new BasePasswordPolicyDTO()
        pdo.setterOne('name', 1l, '', 2, 3, 3, 3)
        pdo.setterTwo(1, 1, 1, true, '', 2, true)
        pdo.setterThree(true, true, 2, true, 3)

        when:
        def result = PasswordPolicyMap.parse(pdo)

        then:
        result != null
    }
}
