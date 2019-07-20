package io.choerodon.websocket.relationship

import io.choerodon.websocket.helper.BrokerHelper
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.socket.WebSocketSession
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap

/**
 * @author dengyouquan
 * */
class DefaultRelationshipDefiningSpec extends Specification {
    private StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
    private BrokerHelper brokerHelper = Mock(BrokerHelper)
    private DefaultRelationshipDefining relationshipDefining =
            new DefaultRelationshipDefining(redisTemplate, brokerHelper)

    def "GetWebSocketSessionsByKey"() {
        when: "调用方法"
        relationshipDefining.getWebSocketSessionsByKey("key")
        then: "校验结果"
        noExceptionThrown()
    }

    def "GetRedisChannelsByKey"() {
        given: "构造请求参数"
        Set<String> survivalChannels = new HashSet<>()
        survivalChannels.add("notify-service")
        survivalChannels.add("iam-service")
        SetOperations<String, Object> setOperations = Mock(SetOperations)

        when: "调用方法"
        relationshipDefining.getRedisChannelsByKey("key", true)
        then: "校验结果"
        noExceptionThrown()
        brokerHelper.getSurvivalBrokers() >> survivalChannels
        1 * brokerHelper.brokerName() >> "notify-service"
        1 * redisTemplate.opsForSet() >> setOperations
        1 * setOperations.members(_) >> { new HashSet<>() }
    }

    def "Contact"() {
        given: "构造请求参数"
        SetOperations<String, Object> setOperations = Mock(SetOperations)
        WebSocketSession session = Mock(WebSocketSession)

        when: "调用方法[null]"
        relationshipDefining.contact(null, null)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        relationshipDefining.contact("key", session)
        then: "校验结果"
        noExceptionThrown()
        1 * brokerHelper.brokerName() >> "notify-service"
        1 * redisTemplate.opsForSet() >> setOperations
    }

    def "RemoveWebSocketSessionContact"() {
        given: "构造请求参数"
        SetOperations<String, Object> setOperations = Mock(SetOperations)
        setOperations.members(_) >> { return new HashSet<>() }
        WebSocketSession session = Mock(WebSocketSession)
        Map<String, Set<WebSocketSession>> keySessionsMap = new ConcurrentHashMap<>()
        Set<WebSocketSession> set = new HashSet<>()
        set.add(session)
        keySessionsMap.put("iam-service", set)


        when: "调用方法[null]"
        relationshipDefining.removeWebSocketSessionContact(null)
        then: "校验结果"
        noExceptionThrown()

    }
}
