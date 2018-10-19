package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.cloud.netflix.zuul.filters.RouteLocator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class RouterOperatorSpec extends Specification {
    RouterOperator routerOperator = new RouterOperator(Mock(ApplicationEventPublisher), Mock(RouteLocator))

    def "RefreshRoutes"() {
        when: "测试"
        routerOperator.refreshRoutes()
        then: "无异常抛出"
        noExceptionThrown()
    }
}
