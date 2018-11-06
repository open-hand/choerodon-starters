package io.choerodon.eureka.event

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
class AbstractEurekaEventObserverSpec extends Specification {

    class EurekaEventObserver extends AbstractEurekaEventObserver {
        def executeNum = new Integer(0)
        def executeDownNum = new Integer(0)

        @Override
        void receiveUpEvent(EurekaEventPayload payload) {
            executeNum++
        }

        @Override
        void receiveDownEvent(EurekaEventPayload payload) {
            executeDownNum++
        }
    }

    @Shared
    def properties = new EurekaEventProperties()

    @Shared
    def observer = new EurekaEventObserver()

    @Shared
    def payload = new EurekaEventPayload()

    def setup() {
        observer.setProperties(properties)
        payload.setAppName('iam-service')
        payload.setStatus('UP')
        payload.setId('abcd')
    }

    def 'test putPayloadInCache' () {
        given: 'set maxCacheSize'
        properties.setMaxCacheSize(1)
        def p1 = new EurekaEventPayload()
        def p2 = new EurekaEventPayload()
        when: ''
        observer.putPayloadInCache(p1)
        then: ''
        observer.getEventCache().size() == 1

        when: ''
        observer.putPayloadInCache(p2)
        then: ''
        observer.getEventCache().size() == 1
        observer.getEventCache().get(0) == p2
    }

    def "test unfinishedEvents"() {
        given: 'put in cache'
        observer.putPayloadInCache(payload)
        when: '执行unfinishedEvents'
        def list = observer.unfinishedEvents(payload.getAppName())

        then:
        list.size() == 1
    }

    def "test retryEvents"() {
        when: 'retry by id'
        def time = observer.executeNum
        observer.retryEvents('abcd', null)
        then:
        observer.executeNum == time + 1
        observer.unfinishedEvents().size() == 0

        when: 'retry by service'
        def time1 = observer.executeNum
        observer.putPayloadInCache(payload)
        observer.retryEvents(null, 'iam-service')
        then:
        observer.executeNum == time1 + 1
        observer.unfinishedEvents().size() == 0

        when: 'retry all'
        observer.putPayloadInCache(payload)
        def time2 = observer.executeNum
        observer.retryEvents(null, null)
        then:
        observer.executeNum == time2 + 1
        observer.unfinishedEvents().size() == 0
    }

    def "test update"() {
        when:
        observer.update(null, payload)
        Thread.sleep(1000)
        then:
        observer.executeNum > 0
    }


    def "test receiveUpEvent"() {
        when:
        def time = observer.executeNum
        observer.receiveUpEvent(payload)
        then:
        observer.executeNum == time + 1
    }

    def "test receiveDownEvent"() {
        when:
        def time = observer.executeDownNum
        observer.receiveDownEvent(payload)
        then:
        observer.executeDownNum == time + 1
    }
}
