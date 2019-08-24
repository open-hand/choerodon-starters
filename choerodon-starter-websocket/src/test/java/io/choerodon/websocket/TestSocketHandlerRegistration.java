package io.choerodon.websocket;

import io.choerodon.websocket.connect.SocketHandlerRegistration;
import io.choerodon.websocket.helper.WebSocketHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TestSocketHandlerRegistration implements SocketHandlerRegistration {
    @Autowired
    private WebSocketHelper helper;
    @Override
    public String path() {
        return "/ws/test";
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return true;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        helper.subscribe("test-topic", session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 啥都不干
    }
}
