package io.choerodon.websocket.helper

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.websocket.CountWebSocketHandler
import io.choerodon.websocket.TestApplication
import io.choerodon.websocket.send.BrokerManager
import io.choerodon.websocket.send.SendMessagePayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketHelperSpec extends Specification {
    static final ObjectMapper MAPPER = new ObjectMapper()
    @LocalServerPort
    Integer masterPort
    @Shared
    Integer slavePort
    @Shared
    CountWebSocketHandler masterCountHandler = new CountWebSocketHandler()
    @Shared
    CountWebSocketHandler slaveCountHandler = new CountWebSocketHandler()
    @Shared
    WebSocketSession masterSession = null
    @Shared
    WebSocketSession slaveSession = null
    @Shared
    ConfigurableApplicationContext secondContext = null

    @Autowired
    BrokerManager brokerManager;

    def setupSpec() {
        secondContext = new SpringApplicationBuilder(TestApplication.class)
                .properties("server.port=0")
                .run()
        slavePort = Integer.valueOf(secondContext.getBean(Environment).getProperty("local.server.port"))
    }

    def cleanupSpec() {

    }

    def "Do Handshake" () {
        when:
        masterCountHandler.afterConnectionEstablishedCount = 0
        masterCountHandler.supportsPartialMessagesCount = 0

        slaveCountHandler.afterConnectionEstablishedCount = 0
        slaveCountHandler.supportsPartialMessagesCount = 0
        def client = new StandardWebSocketClient()
        masterSession = client.doHandshake(masterCountHandler, String.format("ws://localhost:%d/ws/test", masterPort)).get()
        slaveSession = client.doHandshake(slaveCountHandler, String.format("ws://localhost:%d/ws/test", slavePort)).get()
        then:
        noExceptionThrown()
        masterSession != null
        masterCountHandler.afterConnectionEstablishedCount == 1
        masterCountHandler.supportsPartialMessagesCount == 1

        slaveCountHandler.afterConnectionEstablishedCount == 1
        slaveCountHandler.supportsPartialMessagesCount == 1
    }

    def "Send Message" (){
        when:
        synchronized (masterCountHandler){
            synchronized (slaveCountHandler){
                masterCountHandler.handleMessageCount = 0
                slaveCountHandler.handleMessageCount = 0
                SendMessagePayload<String> messagePayload = new SendMessagePayload<>("test", "test-key-slave", "test-data-slave")
                slaveSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                messagePayload.type = "tree" // 测试个不处理的消息
                slaveSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                messagePayload.type = "fish" // 测试个不处理的消息
                messagePayload.key = "test-key-master"
                messagePayload.data = "test-data-master"
                masterSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                messagePayload.type = "test"
                masterSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                slaveCountHandler.wait(1000)
                slaveCountHandler.wait(1000)
            }
            masterCountHandler.wait(1000) //等待一个消息收取
        }
        then:
        noExceptionThrown()
        masterCountHandler.handleMessageCount == 1 // 只收到 test-key-master
        slaveCountHandler.handleMessageCount == 2 // 收到 test-key-master 和 test-key-slave
    }

    def "Session Close" () {
        when:
        masterCountHandler.afterConnectionClosedCount = 0
        slaveCountHandler.afterConnectionClosedCount = 0

        masterSession.close()
        slaveSession.close()

        then:
        noExceptionThrown()
        masterCountHandler.afterConnectionClosedCount == 1
        slaveCountHandler.afterConnectionClosedCount == 1

    }

    def "Broker Shutdown" () {
        when:
        def activeBrokers = brokerManager.getActiveBrokers()
        then:
        activeBrokers.size() > 1
        when:
        secondContext.close()
        Thread.sleep(150)
        activeBrokers = brokerManager.getActiveBrokers()
        then:
        activeBrokers.size() == 1
    }
}
