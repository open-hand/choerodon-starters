package io.choerodon.websocket.send


import io.choerodon.websocket.relationship.RelationshipDefining
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.socket.WebSocketSession
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class DefaultSmartMessageSenderSpec extends Specification {
    private StringRedisTemplate redisTemplate = Mock(StringRedisTemplate)
    private RelationshipDefining relationshipDefining = Mock(RelationshipDefining)
    private DefaultSmartMessageSender messageSender = new DefaultSmartMessageSender(redisTemplate, relationshipDefining)

    def "SendWebSocket"() {
        given: "构造参数"
        WebSocketSendPayload<?> payload = null
        WebSocketSession session = Mock(WebSocketSession)

        when: "调用方法"
        payload = new WebSocketSendPayload<>()
        messageSender.sendWebSocket(session, payload)
        then: "校验结果"
        noExceptionThrown()
        1 * session.isOpen() >> true
        1 * session.sendMessage(_)

        when: "调用方法"
        payload = new WebSocketSendPayload<>()
        messageSender.sendWebSocket(session, payload)
        then: "校验结果"
        noExceptionThrown()
        1 * session.isOpen() >> true
        1 * session.sendMessage(_) >> { throw new IOException("") }
    }

    def "SendRedis"() {
        given: "构造参数"
        WebSocketSendPayload<?> payload = null
        WebSocketSession session = Mock(WebSocketSession)

        when: "调用方法"
        payload = new WebSocketSendPayload<>()
        messageSender.sendRedis("channel", payload)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        payload = new WebSocketSendPayload<>()
        messageSender.sendRedis("channel", payload)
        then: "校验结果"
        noExceptionThrown()
        1 * redisTemplate.convertAndSend(_, _)
    }

    def "SendWebSocket1"() {
        given: "构造参数"
        WebSocketSession session = Mock(WebSocketSession) {
            isOpen() >> true
        }

        when: "调用方法"
        messageSender.sendWebSocket(session, "json")
        then: "校验结果"
        noExceptionThrown()
        1 * session.sendMessage(_)
    }

    def "SendWebSocketByKey"() {
        given: "构造请求参数"
        Set<WebSocketSession> set = new HashSet<>()
        when: "调用方法"
        messageSender.sendWebSocketByKey("key", "json")
        then: "校验结果"
        noExceptionThrown()
        1 * relationshipDefining.getWebSocketSessionsByKey(_) >> set
    }

    def "SendByKey"() {
        given: "构造请求参数"
        Set<WebSocketSession> set = new HashSet<>()
        WebSocketSendPayload<?> payload = new WebSocketSendPayload<>()
        when: "调用方法"
        messageSender.sendByKey("key", payload)
        messageSender.sendByKey("key", "type", "{}")
        then: "校验结果"
        noExceptionThrown()
        2 * relationshipDefining.getWebSocketSessionsByKey(_) >> set
        2 * relationshipDefining.getRedisChannelsByKey(_, _) >> { new HashSet<>() }
    }
}
