package io.choerodon.feign


import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.web.servlet.ModelAndView
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class HystrixHeaderInterceptorSpec extends Specification {
    HystrixHeaderInterceptor interceptor = new HystrixHeaderInterceptor()

    def "PreHandle"() {
        given: "初始化数据"
        def request = new MockHttpServletRequestBuilder(HttpMethod.POST, "https://choerodon.com.cn").header(RequestVariableHolder.HEADER_LABEL, "label").buildRequest(Mock(ServletContext))

        when: "调用preHandle()方法"
        interceptor.preHandle(request, Mock(HttpServletResponse), Mock(Object))

        then: "校验结果"
        noExceptionThrown()
    }

    def "PostHandle"() {
        given: "初始化数据"
        def request = new MockHttpServletRequestBuilder(HttpMethod.POST, "https://choerodon.com.cn").header(RequestVariableHolder.HEADER_LABEL, "label").buildRequest(Mock(ServletContext))
        interceptor.preHandle(request, Mock(HttpServletResponse), Mock(Object))

        when: "调用postHandle()方法"
        interceptor.postHandle(request, Mock(HttpServletResponse), Mock(Object), Mock(ModelAndView))

        then: "期望正常执行"
        noExceptionThrown()
    }
}
