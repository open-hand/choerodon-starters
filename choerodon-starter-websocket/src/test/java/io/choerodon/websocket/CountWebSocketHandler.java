package io.choerodon.websocket;

import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class CountWebSocketHandler implements WebSocketHandler {
    public int afterConnectionEstablishedCount = 0;
    public int handleMessageCount = 0;
    public int handleTransportErrorCount = 0;
    public int afterConnectionClosedCount = 0;
    public int supportsPartialMessagesCount = 0;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        afterConnectionEstablishedCount++;
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        LoggerFactory.getLogger(this.getClass()).info("handleMessage {}, {}", session.getId(), message.getPayload());
        handleMessageCount++;
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        handleTransportErrorCount++;
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        afterConnectionClosedCount++;
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        supportsPartialMessagesCount++;
        synchronized (this){
            this.notify();
        }
        return false;
    }
}
