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
        given:
        def request = new MockHttpServletRequestBuilder(HttpMethod.POST, "https://choerodon.com.cn").header(RequestVariableHolder.HEADER_LABEL, "label").buildRequest(Mock(ServletContext))

        when:
        interceptor.preHandle(request, Mock(HttpServletResponse), Mock(Object))

        then:
        noExceptionThrown()
    }

    def "PostHandle"() {
        given:
        def request = new MockHttpServletRequestBuilder(HttpMethod.POST, "https://choerodon.com.cn").header(RequestVariableHolder.HEADER_LABEL, "label").buildRequest(Mock(ServletContext))
        interceptor.preHandle(request, Mock(HttpServletResponse), Mock(Object))

        when:
        interceptor.postHandle(request, Mock(HttpServletResponse), Mock(Object), Mock(ModelAndView))

        then:
        noExceptionThrown()
    }
}
