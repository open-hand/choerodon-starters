package io.choerodon.websocket.v2.handler;

import io.choerodon.websocket.v2.send.MessageSender;
import io.choerodon.websocket.v2.send.WebSocketSendPayload;
import io.choerodon.websocket.v2.receive.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class HeartBeatMsgHandler implements MessageHandler<String> {

    private static final String HEART_BEAT = "heartBeat";
    private MessageSender messageSender;

    public HeartBeatMsgHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String payload) {
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(HEART_BEAT, null, null));
    }

    @Override
    public String matchType() {
        return HEART_BEAT;
    }

}
