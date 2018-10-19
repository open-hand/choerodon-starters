package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties
import org.springframework.context.annotation.Import
import org.springframework.util.StringUtils
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class MemoryRouteLocatorSpec extends Specification {
    private MemoryRouteLocator memoryRouteLocator

    void setup() {
        String servletPath = "servletPath"
        ZuulProperties properties = Mock(ZuulProperties)
        properties.getPrefix() >> { return "prefix" }
        memoryRouteLocator = new MemoryRouteLocator(servletPath, properties)
    }


    def "LocateRoutes"() {
        given: "准备zuulroute"
        ZuulRoute zuulRoute = new ZuulRoute()
        zuulRoute.setId("1L")
        zuulRoute.setPath("/manager/**")
        zuulRoute.setServiceId("manager-service")
        zuulRoute.setStripPrefix(false)
        zuulRoute.setRetryable(true)
        zuulRoute.setUrl("/manager/**")
        zuulRoute.setCustomSensitiveHeaders(true)
        def sensitiveHeaders = new HashSet<String>()
        zuulRoute.setSensitiveHeaders(sensitiveHeaders)
        zuulRoute.setHelperService("help")
        zuulRoute.setSensitiveHeadersJson("SensitiveHeadersJson")
        and: "setMap"
        def map = new HashMap<String, ZuulProperties.ZuulRoute>()
        map.put("zuul1", zuulRoute)
        MemoryRouteLocator.setMap(map)
        when: "方法调用"
        def routes = memoryRouteLocator.locateRoutes()
        def route = routes.get("/prefix/zuul1")
        then: "无异常抛出"
        StringUtils.hasText(zuulRoute.toString())
        noExceptionThrown()
        MemoryRouteLocator.getMap().equals(map)
        routes.size() == 1
        route.getId().equals(zuulRoute.getId())
        route.getServiceId().equals(zuulRoute.getServiceId())
        route.getPath().equals(zuulRoute.getPath())
        route.getRetryable().equals(zuulRoute.getRetryable())
        route.getSensitiveHeaders().equals(zuulRoute.getSensitiveHeaders())
        route.getUrl().equals(zuulRoute.getUrl())
        route.isStripPrefix().equals(zuulRoute.getStripPrefix())
        route.isStripPrefix().equals(zuulRoute.isStripPrefix())
        route.getSensitiveHeadersJson().equals(zuulRoute.getSensitiveHeadersJson())
        route.getHelperService().equals(zuulRoute.getHelperService())
        route.isCustomSensitiveHeaders().equals(zuulRoute.isCustomSensitiveHeaders())
    }

    def "Refresh"() {
        when: "refresh"
        memoryRouteLocator.refresh()
        then: "无异常"
        noExceptionThrown()
    }
}
