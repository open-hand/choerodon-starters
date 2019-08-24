package io.choerodon.websocket;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.TextMessageHandler;
import io.choerodon.websocket.send.SendMessagePayload;
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
        if (type.equals("test") && data.equals("test-data-1")) {
            helper.sendMessageByKey("test-topic", new SendMessagePayload<>("test", "test-topic", data));
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
