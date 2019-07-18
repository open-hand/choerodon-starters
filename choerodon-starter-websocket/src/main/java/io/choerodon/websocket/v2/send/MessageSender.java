package io.choerodon.websocket.v2.send;

import io.choerodon.websocket.v2.send.WebSocketSendPayload;
import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    void sendWebSocket(WebSocketSession session, WebSocketSendPayload<?> payload);

    void sendWebSocket(WebSocketSession session, String json);

    void sendWebSocketByKey(String key, String json);

    void sendRedis(String channel, WebSocketSendPayload<?> payload);

    void sendByKey(String key, WebSocketSendPayload<?> payload);

    void sendRedis(String channel, String json);

    void sendByKey(String key, String type, String data);

}
