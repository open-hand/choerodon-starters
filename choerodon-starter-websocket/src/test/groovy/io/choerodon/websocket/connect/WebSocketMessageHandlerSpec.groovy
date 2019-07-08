package io.choerodon.websocket.connect

import io.choerodon.websocket.relationship.RelationshipDefining
import io.choerodon.websocket.send.MessageSender
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class WebSocketMessageHandlerSpec extends Specification {
    private MessageSender messageSender = Mock(MessageSender)
    private RelationshipDefining relationshipDefining = Mock(RelationshipDefining)
    private WebSocketMessageHandler handler = new WebSocketMessageHandler(
            Optional.ofNullable(null),
            relationshipDefining, messageSender)

    def "AfterConnectionEstablished"() {
        given: "构造参数"
        WebSocketSession session = Mock(WebSocketSession)
        when: "调用方法"
        handler.afterConnectionEstablished(session)
        then: "校验结果"
        1 * session.getId() >> 1
        noExceptionThrown()
    }

    def "AfterConnectionClosed"() {
        when: "调用方法"
        handler.afterConnectionClosed(null, null)
        then: "校验结果"
        noExceptionThrown()
    }

    def "HandleTransportError"() {
        when: "调用方法"
        handler.handleTransportError(null, null)
        then: "校验结果"
        noExceptionThrown()
    }

    def "HandleTextMessage"() {
        given: "构造参数"
        TextMessage message = new TextMessage("message")
        when: "调用方法"
        handler.handleTextMessage(null, message)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        message = new TextMessage("{}")
        handler.handleTextMessage(null, message)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        message = new TextMessage("{'type':''}")
        handler.handleTextMessage(null, message)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        message = new TextMessage("{'type':'type'}")
        handler.handleTextMessage(null, message)
        then: "校验结果"
        noExceptionThrown()
    }
}
