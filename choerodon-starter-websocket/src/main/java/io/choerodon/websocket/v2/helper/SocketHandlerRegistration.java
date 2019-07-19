package io.choerodon.websocket.v2.helper;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

public interface SocketHandlerRegistration {
    String path();
    boolean doHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse);
    void afterConnectionEstablished(WebSocketSession session);
    void afterConnectionClosed(WebSocketSession session, CloseStatus status);
}
