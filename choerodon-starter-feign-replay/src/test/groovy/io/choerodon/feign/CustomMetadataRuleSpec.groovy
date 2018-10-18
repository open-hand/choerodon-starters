package io.choerodon.feign

import com.netflix.loadbalancer.ILoadBalancer
import com.netflix.loadbalancer.Server
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

    void setup() {
        rule = new CustomMetadataRule()
        loadBalancer = Mock(ILoadBalancer)
        rule.setLoadBalancer(loadBalancer)
    }

    def "Choose"() {
        when:
        rule.choose("iam-service")
        then:
        noExceptionThrown()
//        1 * rule.getPredicate().getEligibleServers(_, _) >> { new ArrayList<Server>() }
        1 * loadBalancer.getAllServers() >> { new ArrayList<Server>() }
    }
}
