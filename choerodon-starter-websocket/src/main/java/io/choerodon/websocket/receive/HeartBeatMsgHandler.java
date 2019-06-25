package io.choerodon.websocket.receive;

import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.send.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class HeartBeatMsgHandler implements ReceiveMsgHandler<String> {

    private static final String HEART_BEAT = "heartBeat";
    private MessageSender messageSender;

    public HeartBeatMsgHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void handle(WebSocketSession session, String payload) {
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(HEART_BEAT, null, null));
    }

    @Override
    public String matchType() {
        return HEART_BEAT;
    }

}
