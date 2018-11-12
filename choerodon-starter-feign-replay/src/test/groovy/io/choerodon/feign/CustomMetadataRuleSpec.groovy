package io.choerodon.feign

import com.netflix.appinfo.InstanceInfo
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault
import com.netflix.loadbalancer.ILoadBalancer
import com.netflix.loadbalancer.Server
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer
import spock.lang.Specification
/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class CustomMetadataRuleSpec extends Specification {
    CustomMetadataRule rule
    ILoadBalancer loadBalancer
    DiscoveryEnabledServer discoveryEnabledServer = Mock(DiscoveryEnabledServer)
    DiscoveryEnabledServer discoveryEnabledServer2 = Mock(DiscoveryEnabledServer)
    InstanceInfo info = Mock(InstanceInfo)
    Map<String, String> metadata
    HystrixRequestVariableDefault<String> hystrixRequestVariableDefault = Mock(HystrixRequestVariableDefault)
    List<Server> servers = new ArrayList<>()

    def setup() {
        rule = new CustomMetadataRule()
        loadBalancer = Mock(ILoadBalancer)
        rule.setLoadBalancer(loadBalancer)
        metadata = new HashMap<>()
        metadata.put("WEIGHT", "1000")
        metadata.put("GROUP", "iam-service,file-service")


        servers.add(discoveryEnabledServer)
        servers.add(discoveryEnabledServer2)
        loadBalancer.getAllServers() >> { servers }
        discoveryEnabledServer.getInstanceInfo() >> { info }
        discoveryEnabledServer2.getInstanceInfo() >> { info }
        info.getMetadata() >> { metadata }
        hystrixRequestVariableDefault.get() >> { "iam-service,file-service" }
    }

    def "Choose"() {
        when: "执行方法"
        rule.choose("iam-service")

        then: "校验结果"
        noExceptionThrown()
    }

    def "choose without metadata"() {
        given: "准备上下文"
        metadata.clear()

        when: "执行方法"
        rule.choose("iam-service")

        then: "校验结果"
        noExceptionThrown()
    }

    def "choose without servers"() {
        given: "准备上下文"
        servers.clear()

        when: "执行方法"
        rule.choose("iam-service")

        then: "校验结果"
        noExceptionThrown()
    }
}
