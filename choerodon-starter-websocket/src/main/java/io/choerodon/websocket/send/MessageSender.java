package io.choerodon.websocket.send;

import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    void sendByKey(String messageKey, SendMessagePayload<?> payload);

    void sendBySession(WebSocketSession session, SendMessagePayload<?> payload);

    void closeSessionByKey(String messageKey);

    void closeLocalSessionByKey(String messageKey);

    void sendToLocalSessionByKey(String messageKey, SendMessagePayload<?> payload);

}
