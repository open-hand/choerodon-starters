package io.choerodon.websocket.send;

import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    void sendByKey(String messageKey, SendMessagePayload<?> payload);

    void sendBySession(WebSocketSession session, SendMessagePayload<?> payload);

    void sendToLocalSessionByKey(String messageKey,SendMessagePayload<?> payload);

}
