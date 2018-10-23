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
//@RunWith(PowerMockRunner)
//@PowerMockRunnerDelegate(Sputnik)
//@PrepareForTest([HystrixRequestContext])
class CustomMetadataRuleSpec extends Specification {
    CustomMetadataRule rule
    ILoadBalancer loadBalancer
    DiscoveryEnabledServer discoveryEnabledServer = Mock(DiscoveryEnabledServer)
    InstanceInfo info = Mock(InstanceInfo)
    Map<String, String> metadata
    HystrixRequestVariableDefault<String> hystrixRequestVariableDefault = Mock(HystrixRequestVariableDefault)

    void setup() {
        rule = new CustomMetadataRule()
        loadBalancer = Mock(ILoadBalancer)
        rule.setLoadBalancer(loadBalancer)
        metadata = new HashMap<>()
        metadata.put("WEIGHT", "1000")
        metadata.put("GROUP", "iam-service,file-service")
    }

    def "Choose"() {
        given: "准备上下文"
        List<Server> servers = new ArrayList<>()
        servers.add(discoveryEnabledServer)
        loadBalancer.getAllServers() >> { servers }
        discoveryEnabledServer.getInstanceInfo() >> { info }
        info.getMetadata() >> { metadata }
        hystrixRequestVariableDefault.get() >> { "iam-service,file-service" }

//        and: "mock静态方法"
//        PowerMockito.mockStatic(HystrixRequestContext)
//        PowerMockito.when(HystrixRequestContext.isCurrentThreadInitialized()).thenReturn(true)
//        PowerMockito.mockStatic(RequestVariableHolder)
//        PowerMockito.when(RequestVariableHolder.LABEL.get()).thenReturn("iam-service,file-service")

        when: "执行方法"
        rule.choose("iam-service")
        then: "校验结果"
        noExceptionThrown()
    }
}
