package io.choerodon.websocket.v2.helper;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class CommonSocketHandler extends TextWebSocketHandler {
    private WebsocketHelper websocketHelper;

    public CommonSocketHandler(WebsocketHelper websocketHelper){
        this.websocketHelper = websocketHelper;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
