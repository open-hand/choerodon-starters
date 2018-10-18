package io.choerodon.core.convertor

import io.choerodon.core.api.dto.UserDTO
import io.choerodon.core.domain.Page
import io.choerodon.core.domain.PageInfo
import io.choerodon.core.domain.core.converter.UserConverter
import io.choerodon.core.domain.core.entity.UserE
import org.junit.runner.RunWith
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
class ConvertPageHelperSpec extends Specification {
    private int size = 10
    DefaultListableBeanFactory beanFactory = Mock(DefaultListableBeanFactory)
    Map<String, ConvertorI> convertorIMap = new HashMap<>()
    UserConverter userConverter = new UserConverter()

    def setup() {
        given: "mock ApplicationContextHelper"
        convertorIMap.put("userConverter", userConverter)
        PowerMockito.mockStatic(ApplicationContextHelper)
        PowerMockito.when(ApplicationContextHelper.getSpringFactory()).thenReturn(beanFactory)
    }

    def "ConvertPage"() {
        given: "构造请求参数"
        PageInfo pageInfo = new PageInfo(1, size, true)
        pageInfo.setTotal(size)
        pageInfo.setBegin(0)
        pageInfo.setEnd(size)
        pageInfo.setSize(pageInfo.getSize())
        pageInfo.setPage(pageInfo.getPage())
        List<UserE> userEList = new ArrayList<>()
        for (int i = 0; i < size; i++) {
            UserE userE = new UserE()
            userEList << userE
        }
        Page page = new Page(userEList, pageInfo, pageInfo.getTotal())

        when: "调用方法"
        def result = ConvertPageHelper.convertPage(page, UserDTO)

        then: "校验结果"
        result.getSize() == size
        result.getTotalPages() == 1
        result.getTotalElements() == size
        result.getContent().size() == size
        beanFactory.getBeansOfType(_) >> { convertorIMap }
        beanFactory.getBean(_) >> { userConverter }
    }
}
