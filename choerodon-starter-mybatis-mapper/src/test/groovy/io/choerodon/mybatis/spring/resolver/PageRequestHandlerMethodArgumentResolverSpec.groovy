package io.choerodon.mybatis.spring.resolver

import io.choerodon.mybatis.pagehelper.annotation.PageableDefault
import io.choerodon.mybatis.pagehelper.domain.PageRequest
import org.powermock.reflect.Whitebox
import org.springframework.core.MethodParameter
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/23.
 */
class PageRequestHandlerMethodArgumentResolverSpec extends Specification {

    def "getDefaultPageRequestFrom"() {
        given:
        SortArgumentResolver sortArgumentResolver = Mock(SortArgumentResolver)
        PageRequestHandlerMethodArgumentResolver resolver = new PageRequestHandlerMethodArgumentResolver(sortArgumentResolver)
        MethodParameter methodParameter = Mock(MethodParameter)
        PageableDefault pageableDefault = Mock(PageableDefault)
        methodParameter.getParameterAnnotation(_) >> pageableDefault
        pageableDefault.page() >> 0
        pageableDefault.size() >> 10
        pageableDefault.sort() >> []

        when:
        PageRequest pageRequest = Whitebox.invokeMethod(resolver, "getDefaultPageRequestFrom", methodParameter)


        then:
        pageRequest.getPage() == 0
        pageRequest.getSize() == 10
    }
}
