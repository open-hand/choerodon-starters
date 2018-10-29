package io.choerodon.oauth.core.password.record

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDO
import io.choerodon.oauth.core.password.domain.BaseLoginHistoryDO
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper
import io.choerodon.oauth.core.password.mapper.BaseLoginHistoryMapper
import spock.lang.Specification

class LoginRecordSpec extends Specification {

    def "test login Error"() {
        given: 'mock mapper'
        def findNullMapper = Mock(BaseLoginAttemptTimesMapper) {
            insertSelective(_) >> 2
        }
        def findMapper = Mock(BaseLoginAttemptTimesMapper) {
            def baseLoginAttemptTimesDO = new BaseLoginAttemptTimesDO()
            baseLoginAttemptTimesDO.setLoginAttemptTimes(10)
            findByUser(_) >> baseLoginAttemptTimesDO
            updateByPrimaryKeySelective(_) >> 2
        }

        when: 'findByUser返回null'
        new LoginRecord(findNullMapper, null).loginError(1L)
        then:
        def exe = thrown(CommonException)
        exe.getCode() == 'error.insert.loginAttempt'

        when: 'findByUser返回不为null'
        new LoginRecord(findMapper, null).loginError(1L)
        then:
        def exe1 = thrown(CommonException)
        exe1.getCode() == 'error.update.loginAttempt'
    }

    def "test login Success"() {
        given: 'mock mapper'
        def findMapper = Mock(BaseLoginAttemptTimesMapper) {
            findByUser(_) >> new BaseLoginAttemptTimesDO()
            updateByPrimaryKeySelective(_) >> 2
        }

        def historyNullMapper = Mock(BaseLoginHistoryMapper) {
            insertSelective(_) >> 2
        }
        def historyMapper = Mock(BaseLoginHistoryMapper) {
            findByUser(_) >> new BaseLoginHistoryDO()
            updateByPrimaryKeySelective(_) >> 2
        }
        def historyNormalMapper = Mock(BaseLoginHistoryMapper) {
            findByUser(_) >> new BaseLoginHistoryDO()
            updateByPrimaryKeySelective(_) >> 1
        }

        when: 'findByUser返回null'
        new LoginRecord(findMapper, historyNullMapper).loginSuccess(1L)

        then:
        def exe = thrown(CommonException)
        exe.getCode() == 'error.insert.loginHistory'

        when: 'findByUser返回不为null'
        new LoginRecord(findMapper, historyMapper).loginSuccess(1L)
        then:
        def exe1 = thrown(CommonException)
        exe1.getCode() == 'error.update.loginHistory'

        when: 'findByUser返回不为null'
        new LoginRecord(findMapper, historyNormalMapper).loginSuccess(1L)
        then:
        def exe2 = thrown(CommonException)
        exe2.getCode() == 'error.update.loginAttempt'

    }
}