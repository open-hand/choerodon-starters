package io.choerodon.websocket.helper

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.websocket.CountWebSocketHandler
import io.choerodon.websocket.TestApplication
import io.choerodon.websocket.send.BrokerManager
import io.choerodon.websocket.send.SendMessagePayload
import io.choerodon.websocket.send.SendPlaintextMessagePayload
import io.choerodon.websocket.send.relationship.BrokerKeySessionMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.Environment
import org.springframework.web.socket.BinaryMessage
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
    BrokerManager brokerManager
    @Autowired
    WebSocketHelper helper
    @Autowired
    BrokerKeySessionMapper sessionMapper

    def setupSpec() {
        secondContext = new SpringApplicationBuilder(TestApplication.class)
                .properties("server.port=0")
                .run()
        slavePort = Integer.valueOf(secondContext.getBean(Environment).getProperty("local.server.port"))
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

    def "Send Binary Message" () {
        when:
        synchronized (masterCountHandler){
            synchronized (slaveCountHandler){
                masterCountHandler.handleBinaryMessageCount = 0
                slaveCountHandler.handleBinaryMessageCount = 0
                slaveSession.sendMessage(new BinaryMessage([1, 2, 3] as byte[]))
                masterSession.sendMessage(new BinaryMessage([1, 2, 3, 4] as byte[]))
                slaveCountHandler.wait(1000)
                slaveCountHandler.wait(1000)
            }
            masterCountHandler.wait(1000) //等待一个消息收取
        }
        then:
        noExceptionThrown()
        masterCountHandler.handleBinaryMessageCount == 1 // 只收到 [1, 2, 3, 4]
        slaveCountHandler.handleBinaryMessageCount == 2 // 收到 [1, 2, 3] 和 [1, 2, 3, 4]
    }

    def "Send PlantText Message" () {
        when:
        synchronized (masterCountHandler){
            synchronized (slaveCountHandler){
                masterCountHandler.handleMessageCount = 0
                slaveCountHandler.handleMessageCount = 0
                slaveSession.sendMessage(new TextMessage("test-data-slave"))
                masterSession.sendMessage(new TextMessage("test-data-master"))
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


    def "Unsubscribe Test" (){
        when:
        sessionMapper.getSessionsByKey("test-key-master").each {
            helper.unsubscribe("test-key-master", it)
        }
        synchronized (masterCountHandler){
            synchronized (slaveCountHandler){
                masterCountHandler.handleMessageCount = 0
                slaveCountHandler.handleMessageCount = 0
                SendMessagePayload<String> messagePayload = new SendMessagePayload<>("test", "test-key-slave", "test-data-slave")
                slaveSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                messagePayload.type = "test"
                messagePayload.key = "test-key-master"
                messagePayload.data = "test-data-master"
                masterSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(messagePayload)))
                slaveCountHandler.wait(1000)
            }
            masterCountHandler.wait(1000) //等待一个消息收取
        }
        then:
        noExceptionThrown()
        masterCountHandler.handleMessageCount == 0 // 收不到
        slaveCountHandler.handleMessageCount == 2 // 收到 test-key-slave 和 test-key-master
    }

    def "Close By Key" () {
        when:
        synchronized (slaveCountHandler){
            masterCountHandler.afterConnectionClosedCount = 0
            slaveCountHandler.afterConnectionClosedCount = 0
            helper.closeSessionByKey("test-key-master")
            slaveCountHandler.wait(1000) //等待一个消息收取
        }
        then:
        noExceptionThrown()
        masterCountHandler.afterConnectionClosedCount == 0 // 因为前面把 master 给取消订阅了
        slaveCountHandler.afterConnectionClosedCount == 1
        masterSession.close()
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
