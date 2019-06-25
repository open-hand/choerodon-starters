package io.choerodon.websocket.relationship;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public interface RelationshipDefining {

    /**
     * 获取与业务key关联的session
     */
    Set<WebSocketSession> getWebSocketSessionsByKey(String key);

    /**
     * 获取于该session关联的业务key
     */
    Set<String> getKeysBySession(WebSocketSession session);

    /**
     * 获取与业务key关联的redis channel
     */
    Set<String> getRedisChannelsByKey(String key, boolean exceptSelf);

    /**
     * 关联key,webSocket,redisChannel
     */
    void contact(String key, WebSocketSession session);

    /**
     * 解除于该webSocket关联的key以及redisChannel
     */
    void removeWebSocketSessionContact(WebSocketSession session);

    /**
     * 解除于该webSocket,key与session的联系
     */
    void removeKeyContact(WebSocketSession session, String key);
}
