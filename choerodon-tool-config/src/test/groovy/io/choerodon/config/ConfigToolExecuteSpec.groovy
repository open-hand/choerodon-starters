package io.choerodon.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.config.domain.Service
import io.choerodon.config.domain.ServiceConfig
import io.choerodon.config.domain.ZuulRoute
import io.choerodon.config.execute.ExecutorFactory
import io.choerodon.config.mapper.ServiceConfigMapper
import io.choerodon.config.mapper.ServiceMapper
import io.choerodon.config.mapper.ZuulRouteMapper
import io.choerodon.config.utils.GatewayProperties
import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationContext
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.security.AccessControlException
import java.security.Permission

/**
 * @author dengyouquan
 * */
class ConfigToolExecuteSpec extends Specification {
    private ServiceMapper serviceMapper = Mock(ServiceMapper)
    private ServiceConfigMapper serviceConfigMapper = Mock(ServiceConfigMapper)
    private ZuulRouteMapper zuulRouteMapper = Mock(ZuulRouteMapper)
    ExecutorFactory executorFactory = new ExecutorFactory()
    GatewayProperties gatewayProperties = new GatewayProperties()
    private ApplicationContext applicationContext = Mock(ApplicationContext)
    private ObjectMapper objectMapper = new ObjectMapper()
    Service service = new Service()
    private ConfigToolExecute configToolExecute
    private ServiceConfig serviceConfig
    private ZuulRoute zuulRoute
    private List<ZuulRoute> routes = new ArrayList<>()
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();


    def setup() {
        service.setId(1L)
        zuulRoute = new ZuulRoute()
        zuulRoute.setId(1L)
        zuulRoute.setName("manager")
        zuulRoute.setPath("/manager/**")
        routes << zuulRoute
        String serviceConfigJson = "{\"creationDate\":1539772375000,\"createdBy\":0,\"lastUpdateDate\":1539772375000,\"lastUpdatedBy\":0,\"objectVersionNumber\":1,\"id\":2,\"name\":\"api-gateway.1539772375123\",\"configVersion\":\"0.1\",\"serviceId\":3,\"value\":\"{\\\"spring.sleuth.integration.enabled\\\":false,\\\"logging.level.com.netflix.discovery.DiscoveryClient\\\":\\\"warn\\\",\\\"ribbon.okhttp.enabled\\\":true,\\\"zuul.sensitiveHeaders\\\":\\\"Cookie,Set-Cookie\\\",\\\"eureka.instance.leaseRenewalIntervalInSeconds\\\":10,\\\"mybatis.mapperLocations\\\":\\\"classpath*:/mapper/*.xml\\\",\\\"spring.cloud.bus.enabled\\\":true,\\\"choerodon.helper.jwt-key\\\":\\\"choerodon\\\",\\\"spring.sleuth.scheduled.enabled\\\":false,\\\"security.ignored[1]\\\":\\\"/**/skip/**\\\",\\\"choerodon.helper.permission.enabled\\\":true,\\\"spring.sleuth.sampler.percentage\\\":1.0,\\\"security.oauth2.resource.userInfoUri\\\":\\\"http://oauth-server/oauth/api/user\\\",\\\"choerodon.helper.permission.cache-size\\\":3000,\\\"spring.cloud.stream.kafka.binder.zkNodes\\\":\\\"127.0.0.1:2181\\\",\\\"eureka.client.serviceUrl.defaultZone\\\":\\\"http://localhost:8000/eureka/\\\",\\\"spring.cloud.stream.bindings.input.default-binder\\\":\\\"kafka\\\",\\\"hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds\\\":10000,\\\"eureka.instance.preferIpAddress\\\":true,\\\"mybatis.configuration.mapUnderscoreToCamelCase\\\":true,\\\"ribbon.httpclient.enabled\\\":false,\\\"eureka.instance.leaseExpirationDurationInSeconds\\\":30,\\\"hystrix.stream.queue.enabled\\\":true,\\\"choerodon.helper.permission.skip-paths\\\":\\\"/**/skip/**, /oauth/**,/prometheus,/health,/env,/metrics\\\",\\\"eureka.client.registryFetchIntervalSeconds\\\":10,\\\"spring.datasource.password\\\":123456,\\\"spring.datasource.username\\\":\\\"choerodon\\\",\\\"security.oauth2.client.grant-type\\\":\\\"client_credentials\\\",\\\"spring.datasource.url\\\":\\\"jdbc:mysql://127.0.0.1/iam_service?useUnicode=true&characterEncoding=utf-8&useSSL=false\\\",\\\"security.ignored[0]\\\":\\\"/oauth/**\\\",\\\"eureka.client.disable-delta\\\":true,\\\"zuul.semaphore.max-semaphores\\\":300,\\\"spring.sleuth.stream.enabled\\\":true,\\\"spring.cloud.stream.bindings.input.destination\\\":\\\"eureka-instance\\\",\\\"choerodon.helper.permission.check-multiply-match\\\":false,\\\"choerodon.helper.oauth-info-uri\\\":\\\"http://oauth-server/oauth/api/user\\\",\\\"zuul.addHostHeader\\\":true,\\\"choerodon.helper.permission.cache-seconds\\\":600,\\\"spring.cloud.stream.kafka.binder.brokers\\\":\\\"127.0.0.1:9092\\\"}\",\"source\":\"工具生成\",\"publicTime\":1539772375000,\"default\":true}"
        serviceConfig = objectMapper.readValue(serviceConfigJson, ServiceConfig)
    }

    def "Run"() {
        given: "构造请求参数"
        //不能放在then下面，需要放在apiGatewayExecutor构造之前
        applicationContext.getBean(_) >> {
            Class aClass ->
                if (aClass.equals(ServiceMapper)) return serviceMapper
                if (aClass.equals(ServiceConfigMapper)) return serviceConfigMapper
                if (aClass.equals(ZuulRouteMapper)) return zuulRouteMapper
        }
        executorFactory.setApplicationContext(applicationContext)
        configToolExecute = new ConfigToolExecute(serviceMapper, executorFactory, gatewayProperties)

        and: "反射注入属性"
        Field configFileName = configToolExecute.getClass().getDeclaredField("configFileName")
        configFileName.setAccessible(true)
        configFileName.set(configToolExecute, "application.yml")

        Field serviceName = configToolExecute.getClass().getDeclaredField("serviceName")
        serviceName.setAccessible(true)
        serviceName.set(configToolExecute, "api-gateway")

        Field serviceVersion = configToolExecute.getClass().getDeclaredField("serviceVersion")
        serviceVersion.setAccessible(true)
        serviceVersion.set(configToolExecute, "0.10")

        ServiceConfig serviceConfig2 = new ServiceConfig()
        BeanUtils.copyProperties(serviceConfig, serviceConfig2)
        Service service2 = new Service()
        BeanUtils.copyProperties(service, service2)

        when: "调用方法"
        //会把AssertionError 0变成CheckExitCalled 1
        exit.expectSystemExit()
        configToolExecute.run(new String[0])

        then: "校验结果"
        thrown(CheckExitCalled)
        zuulRouteMapper.selectAll() >> routes
        serviceMapper.selectOne(_) >> { service }
        serviceConfigMapper.selectOne(_) >> { serviceConfig }
        serviceConfigMapper.updateByPrimaryKeySelective(_) >> 1

        when: "调用方法"
        exit.expectSystemExit()
        configToolExecute.run(new String[0])

        then: "校验结果"
        thrown(CheckExitCalled)
        zuulRouteMapper.selectAll() >> routes
        serviceMapper.selectOne(_) >> { service }
        serviceConfigMapper.insert(_) >> 1
    }
}
