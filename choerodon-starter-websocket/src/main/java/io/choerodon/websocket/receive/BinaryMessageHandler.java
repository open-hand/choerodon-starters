package io.choerodon.websocket.receive;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author hailuo.liu@choerodon.io
 * @date 2019-08-22
 */
public interface BinaryMessageHandler extends MessageHandler {

    default void handle(WebSocketSession session, BinaryMessage message){
    }
}
