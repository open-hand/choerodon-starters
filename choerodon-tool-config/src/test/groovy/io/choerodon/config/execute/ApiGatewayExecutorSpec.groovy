package io.choerodon.config.execute

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.config.domain.ZuulRoute
import io.choerodon.config.mapper.ServiceConfigMapper
import io.choerodon.config.mapper.ServiceMapper
import io.choerodon.config.mapper.ZuulRouteMapper
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationContext
import spock.lang.Specification


/**
 * @author dengyouquan
 * */
class ApiGatewayExecutorSpec extends Specification {
    private ApplicationContext applicationContext = Mock(ApplicationContext)
    private ServiceMapper serviceMapper = Mock(ServiceMapper)
    private ServiceConfigMapper serviceConfigMapper = Mock(ServiceConfigMapper)
    private ZuulRouteMapper zuulRouteMapper = Mock(ZuulRouteMapper)
    private ObjectMapper objectMapper = new ObjectMapper()
    private ApiGatewayExecutor apiGatewayExecutor

    def setup() {

    }

    def "ExecuteInternal"() {
        given: "构造请求参数"
        //不能放在then下面，需要放在apiGatewayExecutor构造之前
        applicationContext.getBean(_) >> {
            Class aClass ->
                if (aClass.equals(ServiceMapper)) return serviceMapper
                if (aClass.equals(ServiceConfigMapper)) return serviceConfigMapper
                if (aClass.equals(ZuulRouteMapper)) return zuulRouteMapper
        }
        apiGatewayExecutor = new ApiGatewayExecutor(applicationContext)
        ZuulRoute zuulRoute = new ZuulRoute()
        zuulRoute.setId(1L)
        zuulRoute.setName("manager")
        zuulRoute.setPath("/manager/**")
        zuulRoute.setServiceId("manager-service")
        zuulRoute.setStripPrefix(false)
        zuulRoute.setRetryable(true)
        zuulRoute.setUrl("/manager/**")
        zuulRoute.setCustomSensitiveHeaders(true)
        zuulRoute.setSensitiveHeaders("rke_header")
        zuulRoute.setHelperService("help")
        ZuulRoute zuulRoute1 = new ZuulRoute()
        BeanUtils.copyProperties(zuulRoute, zuulRoute1)
        List<ZuulRoute> routes = new ArrayList<>()
        routes << zuulRoute
        routes << zuulRoute1
        String mapJson = "{\"spring.datasource.url\":\"jdbc:mysql://127.0.0.1/iam_service?useUnicode=true&characterEncoding=utf-8&useSSL=false\",\"spring.datasource.username\":\"choerodon\",\"spring.datasource.password\":123456,\"spring.sleuth.integration.enabled\":false,\"spring.sleuth.scheduled.enabled\":false,\"spring.sleuth.sampler.percentage\":1.0,\"spring.sleuth.stream.enabled\":true,\"spring.cloud.stream.kafka.binder.brokers\":\"127.0.0.1:9092\",\"spring.cloud.stream.kafka.binder.zkNodes\":\"127.0.0.1:2181\",\"spring.cloud.stream.bindings.input.destination\":\"eureka-instance\",\"spring.cloud.stream.bindings.input.default-binder\":\"kafka\",\"spring.cloud.bus.enabled\":true,\"choerodon.helper.jwt-key\":\"choerodon\",\"choerodon.helper.oauth-info-uri\":\"http://oauth-server/oauth/api/user\",\"choerodon.helper.permission.enabled\":true,\"choerodon.helper.permission.skip-paths\":\"/**/skip/**, /oauth/**,/prometheus,/health,/env,/metrics\",\"choerodon.helper.permission.cache-seconds\":600,\"choerodon.helper.permission.cache-size\":3000,\"choerodon.helper.permission.check-multiply-match\":false,\"mybatis.mapperLocations\":\"classpath*:/mapper/*.xml\",\"mybatis.configuration.mapUnderscoreToCamelCase\":true,\"zuul.addHostHeader\":true,\"zuul.routes.event.path\":\"/event/**\",\"zuul.routes.event.serviceId\":\"event-store-service\",\"zuul.routes.event.helperService\":\"gateway-helper\",\"zuul.routes.devops.path\":\"/devops/**\",\"zuul.routes.devops.serviceId\":\"devops-service\",\"zuul.routes.iam.path\":\"/iam/**\",\"zuul.routes.iam.serviceId\":\"iam-service\",\"zuul.routes.oauth.path\":\"/oauth/**\",\"zuul.routes.oauth.sensitiveHeaders\":null,\"zuul.routes.oauth.serviceId\":\"oauth-server\",\"zuul.routes.oauth.stripPrefix\":false,\"zuul.routes.notify.path\":\"/notify/**\",\"zuul.routes.notify.serviceId\":\"notification-service\",\"zuul.routes.manager.path\":\"/manager/**\",\"zuul.routes.manager.serviceId\":\"manager-service\",\"zuul.routes.file.path\":\"/file/**\",\"zuul.routes.file.serviceId\":\"file-service\",\"zuul.routes.org.path\":\"/org/**\",\"zuul.routes.org.serviceId\":\"organization-service\",\"zuul.semaphore.max-semaphores\":300,\"zuul.sensitiveHeaders\":\"Cookie,Set-Cookie\",\"eureka.instance.preferIpAddress\":true,\"eureka.instance.leaseRenewalIntervalInSeconds\":10,\"eureka.instance.leaseExpirationDurationInSeconds\":30,\"eureka.client.serviceUrl.defaultZone\":\"http://localhost:8000/eureka/\",\"eureka.client.registryFetchIntervalSeconds\":10,\"eureka.client.disable-delta\":true,\"ribbon.httpclient.enabled\":false,\"ribbon.okhttp.enabled\":true,\"hystrix.stream.queue.enabled\":true,\"hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds\":10000,\"security.oauth2.client.grant-type\":\"client_credentials\",\"security.oauth2.resource.userInfoUri\":\"http://oauth-server/oauth/api/user\",\"security.ignored[0]\":\"/oauth/**\",\"security.ignored[1]\":\"/**/skip/**\",\"logging.level.com.netflix.discovery.DiscoveryClient\":\"warn\"}"
        Map<String, Object> map = objectMapper.readValue(mapJson, HashMap)

        when: "调用方法"
        apiGatewayExecutor.executeInternal(map)

        then: "校验结果"
        noExceptionThrown()
        zuulRouteMapper.selectAll() >> routes
    }
}
