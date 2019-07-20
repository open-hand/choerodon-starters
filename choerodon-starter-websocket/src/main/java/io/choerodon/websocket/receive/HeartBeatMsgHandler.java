package io.choerodon.websocket.receive;

import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.send.WebSocketSendPayload;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class HeartBeatMsgHandler implements MessageHandler<String> {

    private static final String HEART_BEAT = "heartBeat";
    private WebSocketHelper webSocketHelper;

    public HeartBeatMsgHandler(@Lazy WebSocketHelper webSocketHelper) {
        this.webSocketHelper = webSocketHelper;
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String payload) {
        webSocketHelper.contact(session, key);
        webSocketHelper.sendMessageBySession(session, new WebSocketSendPayload<>(HEART_BEAT, null, null));
    }

    @Override
    public String matchType() {
        return HEART_BEAT;
    }

}
