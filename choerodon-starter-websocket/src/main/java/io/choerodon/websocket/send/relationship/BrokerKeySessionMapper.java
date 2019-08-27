package io.choerodon.websocket.send.relationship;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public interface BrokerKeySessionMapper {

    /**
     *
     * @param messageKey  订阅的Key
     * @param session  接收消息的session
     */
    void subscribe(String messageKey, WebSocketSession session);

    /**
     * 解除session的所有订阅key
     * @param session web socket session
     */
    void unsubscribeAll(WebSocketSession session);

    /**
     * 解除session订阅的key
     * @param session web socket session
     * @param messageKey message key
     */
    void unsubscribe(String messageKey, WebSocketSession session);

    /**
     * 获取 key 关联的所有本实例内的 session
     * @param key message key
     * @return key 关联的所有本实例内的 session
     */
    Set<WebSocketSession> getSessionsByKey(String key);

    /**
     * 获取订阅了 key 的所有 redis 通道
     * @param key message key
     * @return 订阅了 key 的所有 redis 通道
     */
    Set<String> getBrokerChannelsByKey(String key);

    /**
     * 获取订阅了 key 的除自己外所有 redis 通道
     * @param messageKey message key
     * @param exceptSelf 是否排除自己
     * @return 订阅了 key 的除自己外所有 redis 通道
     */
    Set<String> getBrokerChannelsByKey(String messageKey,boolean exceptSelf);

    /**
     * 获取于该session关联的业务key
     * @param session web socket session
     * @return session关联的业务key
     */
    Set<String> getKeysBySession(WebSocketSession session);

}
