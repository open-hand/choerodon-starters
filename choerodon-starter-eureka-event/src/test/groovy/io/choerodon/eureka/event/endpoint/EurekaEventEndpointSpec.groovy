package io.choerodon.eureka.event.endpoint

import spock.lang.Specification


class EurekaEventEndpointSpec extends Specification {

    def eurekaEventService = Mock(EurekaEventService)

    def eurekaEventEndpoint = new EurekaEventEndpoint(eurekaEventService)


    def "test list"() {
        when:
        eurekaEventEndpoint.list('')
        then:
        1 * eurekaEventService.unfinishedEvents(_)
    }

    def "test retry"() {
        when:
        eurekaEventEndpoint.retry('', '')
        then:
        1 * eurekaEventService.retryEvents(_, _)
    }
}
