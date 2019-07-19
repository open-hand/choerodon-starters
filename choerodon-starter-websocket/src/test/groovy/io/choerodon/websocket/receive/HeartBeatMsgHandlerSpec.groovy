package io.choerodon.websocket.receive

import io.choerodon.websocket.v2.send.MessageSender
import io.choerodon.websocket.v2.receive.HeartBeatMsgHandler
import io.choerodon.websocket.v2.receive.WebSocketReceivePayload
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class HeartBeatMsgHandlerSpec extends Specification {
    private MessageSender messageSender = Mock(MessageSender)
    private HeartBeatMsgHandler handler = new HeartBeatMsgHandler(messageSender)

    def "Handle"() {
        given: "构造请求参数"
        WebSocketReceivePayload payload = new WebSocketReceivePayload()
        payload.setType("type")
        payload.setData("data")
        WebSocketReceivePayload payload2 = new WebSocketReceivePayload("type", "data")

        when: "调用方法"
        handler.handle(null, null, null, null)
        then: "校验结果"
        noExceptionThrown()
        1 * messageSender.sendWebSocket(_, _)
        payload.equals(payload2)
    }
}
