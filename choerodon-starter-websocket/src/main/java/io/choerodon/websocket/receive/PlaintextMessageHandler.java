package io.choerodon.websocket.receive;

import org.springframework.web.socket.WebSocketSession;

public interface PlaintextMessageHandler extends MessageHandler{

    default void handle(WebSocketSession session, String payload){

    }

}
