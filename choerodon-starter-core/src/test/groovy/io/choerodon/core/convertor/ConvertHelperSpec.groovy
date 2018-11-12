package io.choerodon.core.convertor

import io.choerodon.core.api.dto.UserDTO
import io.choerodon.core.domain.core.converter.UserConverter
import io.choerodon.core.domain.core.entity.UserE
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author dengyouquan
 * */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([ApplicationContextHelper])
@Stepwise
class ConvertHelperSpec extends Specification {
    DefaultListableBeanFactory beanFactory = Mock(DefaultListableBeanFactory)
    Map<String, ConvertorI> convertorIMap = new HashMap<>()
    UserConverter userConverter = new UserConverter()

    def setup() {
        given: "mock ApplicationContextHelper"
        convertorIMap.put("userConverter", userConverter)
        PowerMockito.mockStatic(ApplicationContextHelper)
        PowerMockito.when(ApplicationContextHelper.getSpringFactory()).thenReturn(beanFactory)
    }

    def "ConvertList"() {
        given: "构造请求参数"
        UserE userE = new UserE()
        userE.setId(1L)
        userE.setName("name")
        List<UserE> userEList = new ArrayList<>()
        userEList << userE

        when: "调用方法[null]"
        def result = ConvertHelper.convertList(null, Object)
        then: "校验结果"
        result.isEmpty()

        when: "调用方法"
        result = ConvertHelper.convertList(userEList, UserDTO)
        then: "校验结果"
        result.size() == 1
        result.get(0).getId().equals(userE.getId())
        result.get(0).getName().equals(userE.getName())
        1 * beanFactory.getBeansOfType(_) >> { convertorIMap }
        1 * beanFactory.getBean(_) >> { userConverter }
        0 * _
    }

    def "Convert"() {
        given: "构造请求参数"
        UserE userE = new UserE()
        userE.setId(1L)
        userE.setName("name")

        when: "调用方法[null]"
        def result = ConvertHelper.convert(null, Object)
        then: "校验结果"
        result == null

        when: "调用方法"
        result = ConvertHelper.convert(userE, UserDTO)
        then: "校验结果"
        result instanceof UserDTO
        result.getId().equals(userE.getId())
        result.getName().equals(userE.getName())
        //DestinClassData有缓存
        0 * beanFactory.getBeansOfType(_) >> { convertorIMap }
        0 * beanFactory.getBean(_) >> { userConverter }
        0 * _
    }
}
