package io.choerodon.websocket;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.BinaryMessageHandler;
import io.choerodon.websocket.send.SendBinaryMessagePayload;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TestBinaryHandler implements BinaryMessageHandler {
    private WebSocketHelper helper;

    public TestBinaryHandler(@Lazy WebSocketHelper helper) {
        this.helper = helper;
    }


    @Override
    public void handle(WebSocketSession session, BinaryMessage message) {
        // 消息长度为偶数则广播，否则为响应
        if (message.getPayloadLength() % 2 == 0){
            helper.sendMessageByKey("test-key-master", new SendBinaryMessagePayload("test", "test-key-master", message.getPayload().array()));
        } else {
            helper.sendMessageBySession(session, new SendBinaryMessagePayload("test", "test-key-master", message.getPayload().array()));
        }
    }

    @Override
    public String matchPath() {
        return "/ws/test";
    }
}
