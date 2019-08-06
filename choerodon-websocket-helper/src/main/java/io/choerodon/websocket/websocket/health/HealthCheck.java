package io.choerodon.websocket.websocket.health;

import io.choerodon.websocket.session.Session;
import org.springframework.web.socket.WebSocketMessage;


/**
 * Health check, responsible for monitoring connection availability.
 * @version 0.1 2019-07-26 12:07
 * @author dongbin
 * @since 1.8
 */
public interface HealthCheck {

    /**
     * created.
     * @param session target.
     */
    void onCreated(Session session);

    /**
     * received message
     * @param session target.
     */
    void onReceived(Session session, WebSocketMessage msg);

    /**
     * closed
     * @param session target.
     */
    void onClosed(Session session);


}
