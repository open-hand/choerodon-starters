package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigServerInstanceProviderSpec extends Specification {
    def "GetConfigServerInstance"() {
        given: "参数准备"
        def client = Mock(DiscoveryClient)
        client.getInstances(_) >> {
            def list = new ArrayList<ServiceInstance>()
            def serviceInstance = Mock(ServiceInstance)
            list.add(serviceInstance)
            return list
        }
        def configServerInstanceProvider = new ConfigServerInstanceProvider(client)
        when: "方法调用"
        configServerInstanceProvider.getConfigServerInstance("serviceId")
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "GetConfigServerInstance[Exception]"() {
        given: "参数准备"
        def serviceId = "serviceId"
        def client = Mock(DiscoveryClient)
        client.getInstances(_) >> {
            return new ArrayList<ServiceInstance>()
        }
        def configServerInstanceProvider = new ConfigServerInstanceProvider(client)
        when: "方法调用"
        configServerInstanceProvider.getConfigServerInstance(serviceId)
        then: "无异常抛出"
        def e = thrown(IllegalStateException)
        e.message == "No instances found of configserver (" + serviceId + ")"
    }

}
