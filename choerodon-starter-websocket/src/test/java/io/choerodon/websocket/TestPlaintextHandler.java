package io.choerodon.websocket;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.PlaintextMessageHandler;
import io.choerodon.websocket.receive.TextMessageHandler;
import io.choerodon.websocket.send.SendMessagePayload;
import io.choerodon.websocket.send.SendPlaintextMessagePayload;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TestPlaintextHandler implements PlaintextMessageHandler {
    private WebSocketHelper helper;

    public TestPlaintextHandler(@Lazy WebSocketHelper helper) {
        this.helper = helper;
    }

    @Override
    public void handle(WebSocketSession session, String payload) {
        // master 的测试消息会广播， 其他测试消息仅回响
        if (payload.equals("test-data-master")) {
            LoggerFactory.getLogger(this.getClass()).info("sendMessageByKey {}", payload);
            helper.sendMessageByKey("test-key-master", new SendPlaintextMessagePayload(payload));
        } else {
            LoggerFactory.getLogger(this.getClass()).info("sendMessageBySession {}", payload);
            helper.sendMessageBySession(session, new SendPlaintextMessagePayload(payload));
        }
    }

    @Override
    public String matchPath() {
        return "/ws/test";
    }
}
