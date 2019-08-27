package io.choerodon.websocket;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.TextMessageHandler;
import io.choerodon.websocket.send.SendMessagePayload;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TestTextHandler implements TextMessageHandler<String> {
    private WebSocketHelper helper;

    public TestTextHandler(@Lazy WebSocketHelper helper) {
        this.helper = helper;
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String data) {
        // master 的测试消息会广播， 其他测试消息仅回响
        if (type.equals("test") && key.equals("test-key-master")) {
            LoggerFactory.getLogger(TestTextHandler.class).info("WebSocketSession {}, {}, {}", type, key, data);
            helper.sendMessageByKey("test-key-master", new SendMessagePayload<>(type, key, data));
        } else {
            LoggerFactory.getLogger(TestTextHandler.class).info("WebSocketSession {}, {}, {}", type, key, data);
            helper.sendMessageBySession(session, new SendMessagePayload<>(type, key, data));
        }
    }

    @Override
    public String matchType() {
        return "test";
    }

    @Override
    public String matchPath() {
        return "/ws/test";
    }
}
