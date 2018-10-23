package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.discovery.event.HeartbeatEvent
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class DiscoveryClientConfigServiceBootstrapConfigurationSpec extends Specification {
    DiscoveryClientConfigServiceBootstrapConfiguration discoveryBootstrapConfig = new DiscoveryClientConfigServiceBootstrapConfiguration()
    private ConfigClientProperties config = Mock(ConfigClientProperties)
    private ConfigServerInstanceProvider instanceProvider = Mock(ConfigServerInstanceProvider)

    void setup() {
        discoveryBootstrapConfig.setConfig(config)
        discoveryBootstrapConfig.setInstanceProvider(instanceProvider)
        discoveryBootstrapConfig.setMonitor(Mock(HeartbeatMonitor))
    }

    def "ConfigServerInstanceProvider"() {
        when: "方法调用"
        def provider = discoveryBootstrapConfig.configServerInstanceProvider(Mock(DiscoveryClient))
        then: 'ConfigServerInstanceProvider返回'
        noExceptionThrown()
        provider instanceof ConfigServerInstanceProvider
    }

    def "Startup"() {
        given: "mock"
        config.getDiscovery() >> {
            def discovery = new ConfigClientProperties.Discovery()
            discovery.setServiceId("serviceId")
            return discovery
        }
        instanceProvider.getConfigServerInstance("serviceId") >> {
            def server = Mock(ServiceInstance)
            server.getUri() >> { return new URI("path") }
            server.getMetadata() >> {
                def set = new HashMap<String, String>()
                set.put("password", "pwd")
                set.put("configPath", "/configPath")
                set.put("user", "")
                return set
            }
            return server
        }
        when: "方法调用"
        discoveryBootstrapConfig.startup(Mock(ContextRefreshedEvent))
        then: 'ConfigServerInstanceProvider返回'
        noExceptionThrown()
    }

    def "Heartbeat"() {
        when: "方法调用"
        discoveryBootstrapConfig.heartbeat(Mock(HeartbeatEvent))
        then: "无异常"
        noExceptionThrown()
    }
}
