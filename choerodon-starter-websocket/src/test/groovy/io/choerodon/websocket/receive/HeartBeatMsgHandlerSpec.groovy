package io.choerodon.websocket.receive

import io.choerodon.websocket.helper.WebSocketHelper
import io.choerodon.websocket.send.MessageSender
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class HeartBeatMsgHandlerSpec extends Specification {
    private WebSocketHelper webSocketHelper = Mock(WebSocketHelper)
    private HeartBeatMsgHandler handler = new HeartBeatMsgHandler(webSocketHelper)

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
        1 * webSocketHelper.sendMessageBySession(_, _)
        payload.equals(payload2)
    }
}
