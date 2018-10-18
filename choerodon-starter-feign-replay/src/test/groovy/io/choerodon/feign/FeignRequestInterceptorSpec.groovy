package io.choerodon.feign

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext
import feign.RequestTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import spock.lang.Specification

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class FeignRequestInterceptorSpec extends Specification {
    FeignRequestInterceptor interceptor

    void setup() {
        def properties = new CommonProperties()
//        properties.serviceAccountId = 1L
        interceptor = new FeignRequestInterceptor(properties)
        Method method = interceptor.getClass().getDeclaredMethod("init")
        method.setAccessible(true)
        method.invoke(interceptor)
    }

    def "Apply without security context"() {
        given:
        def request = new RequestTemplate()
        Constructor<HystrixRequestContext> hystrixRequestContextConstructor = HystrixRequestContext.getDeclaredConstructor()
        hystrixRequestContextConstructor.setAccessible(true)
        def hystrix = hystrixRequestContextConstructor.newInstance()
        Field field = hystrix.getClass().getDeclaredField("state")
        field.setAccessible(true)
        field.set(hystrix, new ConcurrentHashMap<>())
        HystrixRequestContext.setContextOnCurrentThread(hystrix)

        when:
        interceptor.apply(request)
        Field headers = request.getClass().getDeclaredField("headers")
        headers.setAccessible(true)
        def value = (Map<String, Collection<String>>) headers.get(request)
        then:
        value != null
    }

    def "Apply with security context"() {
        given:
        def request = new RequestTemplate()
        SecurityContextHolder.setContext(new SecurityContextImpl())
        SecurityContextHolder.getContext().setAuthentication(Mock(Authentication))

        when:
        interceptor.apply(request)
        Field headers = request.getClass().getDeclaredField("headers")
        headers.setAccessible(true)
//        def value = (Map<String, Collection<String>>) headers.get(request)
//        PowerMockito.when(interceptor.apply(PowerMockito.mock(RequestTemplate)))
        then:
//        value != null
        thrown(NullPointerException)
//        value.get(RequestVariableHolder.HEADER_LABEL) != null
    }

}
