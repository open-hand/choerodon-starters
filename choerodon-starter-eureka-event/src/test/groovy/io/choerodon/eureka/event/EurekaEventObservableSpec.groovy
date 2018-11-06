package io.choerodon.eureka.event

import spock.lang.Specification

class EurekaEventObservableSpec extends Specification {
    def "test sendEvent"() {
        given:
        def observable = new EurekaEventObservable()
        def observer = Mock(Observer)
        observable.addObserver(observer)
        when:
        observable.sendEvent(new EurekaEventPayload())
        then:
        1 * observer.update(_, _)
    }
}
