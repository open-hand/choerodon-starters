package io.choerodon.oauth.core.password.record

import io.choerodon.core.exception.CommonException
import io.choerodon.oauth.core.password.domain.BaseLoginAttemptTimesDTO
import io.choerodon.oauth.core.password.mapper.BaseLoginAttemptTimesMapper
import io.choerodon.oauth.core.password.mapper.BasePasswordHistoryMapper
import spock.lang.Specification

class PasswordRecordSpec extends Specification {
    def "test update Password"() {
        given: 'mock mapper'
        def findMapper = Mock(BaseLoginAttemptTimesMapper) {
            findByUser(_) >> new BaseLoginAttemptTimesDTO()
            updateByPrimaryKeySelective(_) >> 2
        }

        def findNullMapper = Mock(BaseLoginAttemptTimesMapper)

        def historyMapper = Mock(BasePasswordHistoryMapper) {
            insertSelective(_) >> 2
        }

        when: 'findByUser不为null，update异常'
        new PasswordRecord(findMapper, null).updatePassword(1L, 'old')
        then:
        def exe = thrown(CommonException)
        exe.getCode() == 'error.update.password_record'

        when: 'findByUser为null，insert异常'
        new PasswordRecord(findNullMapper, historyMapper).updatePassword(1L, 'old')
        then:
        def exe1 = thrown(CommonException)
        exe1.getCode() == 'error.insert.passwordHistory'
    }

    def "test un Lock User"() {
        given: 'mock mapper'
        def findMapper = Mock(BaseLoginAttemptTimesMapper) {
            findByUser(_) >> new BaseLoginAttemptTimesDTO()
            updateByPrimaryKeySelective(_) >> 2
        }
        when: 'findByUser不为null，update异常'
        new PasswordRecord(findMapper, null).unLockUser(1L)
        then:
        def exe = thrown(CommonException)
        exe.getCode() == 'error.update.loginAttempt'
    }
}