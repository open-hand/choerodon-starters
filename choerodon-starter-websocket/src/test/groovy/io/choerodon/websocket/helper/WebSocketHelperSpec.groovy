package io.choerodon.websocket.helper

import io.choerodon.websocket.CountWebSocketHandler
import io.choerodon.websocket.TestApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise


/**
 * Created by hailuo.liu@choerodon.io on 2019-08-23.
 */
@Stepwise
class WebSocketHelperSpec extends Specification {
    @Shared CountWebSocketHandler mockHandler = new CountWebSocketHandler()
    @Shared WebSocketSession session = null
    @Shared ConfigurableApplicationContext context = null

    def setupSpec() {
        SpringApplicationBuilder send = new SpringApplicationBuilder(TestApplication.class)
        .properties("server.port=54345")
        context = send.run()
    }

    def cleanupSpec() {
        context.close()
    }

    def "Do Handshake" () {
        when:
        mockHandler.afterConnectionEstablishedCount = 0
        mockHandler.supportsPartialMessagesCount = 0
        def client = new StandardWebSocketClient()
        session = client.doHandshake(mockHandler, "ws://localhost:54345/ws/test").get()
        then:
        noExceptionThrown()
        session != null
        mockHandler.afterConnectionEstablishedCount == 1
        mockHandler.supportsPartialMessagesCount == 1
    }

    def "Send Message" (){
        when:
        synchronized (mockHandler){
            mockHandler.handleMessageCount = 0
            session.sendMessage(new TextMessage("{\"type\":\"test\", \"data\":\"test-data-1\"}"))
            mockHandler.wait(1000) //等待处理异步消息收发
        }
        then:
        noExceptionThrown()
        mockHandler.handleMessageCount == 1
    }

    def "Session Close" () {
        when:
        mockHandler.afterConnectionClosedCount = 0
        session.close()
        then:
        noExceptionThrown()
        mockHandler.afterConnectionClosedCount == 1
    }
}
