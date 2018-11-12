package io.choerodon.eureka.event

import com.netflix.appinfo.InstanceInfo
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class EurekaEventHandlerSpec extends Specification {

    def handler = EurekaEventHandler.instance


    def "test eurekaAddInstance"() {
        given: 'Mock InstanceInfo'
        def instanceInfo = Mock(InstanceInfo) {
            getId() >> 'abcd'
            getStatus() >> InstanceInfo.InstanceStatus.UP
            getAppName() >> ''
            getMetadata() >> new HashMap<String, String>()
        }
        when:
        handler.eurekaAddInstance(instanceInfo)
        then:
        handler.getServiceInstanceIds().size() == 1
    }

    def "test eurekaRemoveInstance"() {
        given: 'Mock InstanceInfo'
        def instanceInfo = Mock(InstanceInfo) {
            getId() >> 'abcd'
            getStatus() >> InstanceInfo.InstanceStatus.DOWN
            getAppName() >> ''
            getMetadata() >> new HashMap<String, String>()
        }

        when:
        handler.eurekaRemoveInstance(instanceInfo)
        then:
        handler.getServiceInstanceIds().isEmpty()
    }

    def "test getObservable"() {
        when:
        def observable = handler.getObservable()
        then:
        observable != null
    }

    def "test init"() {
        when:
        handler.init()
        then:
        noExceptionThrown()
    }
}
