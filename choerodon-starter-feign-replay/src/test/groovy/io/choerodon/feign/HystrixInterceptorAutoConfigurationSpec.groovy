package io.choerodon.feign

import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class HystrixInterceptorAutoConfigurationSpec extends Specification {
    def "AddInterceptors"() {
        given:
        InterceptorRegistry interceptorRegistry = Mock(InterceptorRegistry)
        HystrixInterceptorAutoConfiguration configuration = new HystrixInterceptorAutoConfiguration()

        when:
        configuration.addInterceptors(interceptorRegistry)

        then:
        noExceptionThrown()
    }
}
