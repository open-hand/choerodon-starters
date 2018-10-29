package io.choerodon.oauth.core.password

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDO
import io.choerodon.oauth.core.password.domain.BaseUserDO
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper
import io.choerodon.oauth.core.password.validator.login.MaxErrorTimeStrategy
import io.choerodon.oauth.core.password.validator.password.MinLengthStrategy
import spock.lang.Specification

class PasswordPolicyManagerSpec extends Specification {

    def "test password Validate"() {
        given: '创建测试需要的passwordStrategyStore'
        def passwordStrategyStore = new PasswordStrategyStore(null)
        passwordStrategyStore.getStrategyMap().put(PasswordPolicyType.MIN_LENGTH.getValue(), new MinLengthStrategy())
        def policy = new BasePasswordPolicyDO()
        policy.setMinLength(5)
        policy.setEnablePassword(true)
        policy.setEnableSecurity(false)

        when:
        new PasswordPolicyManager(passwordStrategyStore, null).passwordValidate('abcd', null, policy)

        then:
        def exe = thrown CommonException
        exe.code.contains('error.password.policy.minLength')
    }

    def "test is Need Captcha"() {
        given: '创建测试需要的passwordStrategyStore'
        def baseLoginAttemptTimesMapper = Mock(BaseLoginAttemptTimesMapper) {
            def baseLoginAttemptTimesDO = new BaseLoginAttemptTimesDO()
            baseLoginAttemptTimesDO.setLoginAttemptTimes(10)
            findByUser(_) >> baseLoginAttemptTimesDO
        }
        def passwordStrategyStore = new PasswordStrategyStore(null)
        def policy = new BasePasswordPolicyDO()
        policy.setMaxCheckCaptcha(5)
        policy.setEnableCaptcha(true)
        policy.setEnableSecurity(true)
        policy.setEnablePassword(false)

        when:
        def result = new PasswordPolicyManager(passwordStrategyStore, baseLoginAttemptTimesMapper).isNeedCaptcha(policy, new BaseUserDO())

        then:
        result
    }

    def "test login Validate"() {
        given: '创建测试需要的passwordStrategyStore'
        def loginStrategy = Mock(MaxErrorTimeStrategy)
        def passwordStrategyStore = new PasswordStrategyStore(null)
        passwordStrategyStore.getStrategyMap().put(PasswordPolicyType.MAX_ERROR_TIME.getValue(), loginStrategy)
        def policy = new BasePasswordPolicyDO()
        policy.setEnableSecurity(true)
        policy.setEnablePassword(false)
        policy.setMaxErrorTime(5)

        when:
        new PasswordPolicyManager(passwordStrategyStore, null).loginValidate('abcd', null, policy)

        then:
        1 * loginStrategy.validate(_, _, _)
    }
}