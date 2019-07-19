package io.choerodon.websocket.notify

import io.choerodon.websocket.v2.receive.ReceiveRedisMessageListener
import io.choerodon.websocket.v2.send.MessageSender
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class ReceiveRedisMessageListenerSpec extends Specification {
    private MessageSender messageSender = Mock(MessageSender)
    private ReceiveRedisMessageListener redisMessageListener =
            new ReceiveRedisMessageListener(messageSender)

    def "ReceiveMessage"() {
        given: "构造请求参数"
        def message = "{'key':'value'}"

        when: "调用方法[警告]"
        redisMessageListener.receiveMessage(1L)
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法[警告]"
        redisMessageListener.receiveMessage("message")
        then: "校验结果"
        noExceptionThrown()

        when: "调用方法[警告]"
        redisMessageListener.receiveMessage(message)
        then: "校验结果"
        noExceptionThrown()
        1 * messageSender.sendWebSocketByKey(_, _)
    }
}
