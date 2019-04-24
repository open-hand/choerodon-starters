package io.choerodon.oauth.core.password.service.impl

import io.choerodon.oauth.core.password.domain.BaseUserDTO
import io.choerodon.oauth.core.password.mapper.BaseUserMapper
import spock.lang.Specification

class BaseUserServiceImplSpec extends Specification {

    def "test lock User"() {
        given: 'Mock mapper'
        def baseUserMapper = Mock(BaseUserMapper) {
            selectByPrimaryKey(_) >> new BaseUserDTO()
            updateByPrimaryKeySelective(_) >> 1
        }
        when:
        def result = new BaseUserServiceImpl(baseUserMapper).lockUser(1, 100)
        then:
        result != null
    }
}