package io.choerodon.mybatis.provider.special

import io.choerodon.mapper.RoleDO
import io.choerodon.mybatis.IntegrationTestConfiguration
import io.choerodon.mybatis.helper.MapperHelper
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/10/24.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SpecialProviderSpec extends Specification {
    def "InsertUseGeneratedKeys"() {
        given:
        SpecialProvider specialProvider = new SpecialProvider(null, new MapperHelper())
        SpecialProvider spy = Mockito.spy(specialProvider)
        Mockito.doReturn(RoleDO.class).when(spy).getEntityClass(Mockito.anyObject())

        when:
        String str = spy.insertUseGeneratedKeys(null)

        then:
        str.contains("#{enabled,javaType=java.lang.Boolean}")
    }
}
