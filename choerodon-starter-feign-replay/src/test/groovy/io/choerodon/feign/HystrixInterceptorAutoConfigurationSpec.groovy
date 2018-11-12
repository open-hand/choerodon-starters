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
        given: "配置调用环境"
        InterceptorRegistry interceptorRegistry = Mock(InterceptorRegistry)
        HystrixInterceptorAutoConfiguration configuration = new HystrixInterceptorAutoConfiguration()

        when: "调用方法"
        configuration.addInterceptors(interceptorRegistry)

        then: "期望无异常"
        noExceptionThrown()
    }
}
