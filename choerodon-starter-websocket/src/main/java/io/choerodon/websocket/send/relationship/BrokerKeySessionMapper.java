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
     * @param session
     */
    void unsubscribeAll(WebSocketSession session);

    /**
     * 解除session订阅的key
     * @param session
     * @param messageKey
     */
    void unsubscribe(String messageKey, WebSocketSession session);

    /**
     *
     * @param key
     * @return
     */
    Set<WebSocketSession> getSessionsByKey(String key);

    /**
     *
     * @param key
     * @return
     */
    Set<String> getBrokerChannelsByKey(String key);

    /**
     *
     * @param messageKey
     * @param exceptSelf
     * @return
     */
    Set<String> getBrokerChannelsByKey(String messageKey,boolean exceptSelf);

    /**
     * 获取于该session关联的业务key
     */
    Set<String> getKeysBySession(WebSocketSession session);

}
