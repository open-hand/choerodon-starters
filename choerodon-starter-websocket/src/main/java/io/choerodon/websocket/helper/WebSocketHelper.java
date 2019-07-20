package io.choerodon.websocket.helper;

import io.choerodon.websocket.relationship.RelationshipDefining;
import io.choerodon.websocket.receive.WebSocketMessageHandler;
import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.receive.MessageHandler;
import io.choerodon.websocket.send.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
@Component
public class WebSocketHelper {
    private WebSocketMessageHandler handler;
    private MessageSender sender;
    private RelationshipDefining relationshipDefining;

    public WebSocketHelper(WebSocketMessageHandler handler, MessageSender sender, RelationshipDefining relationshipDefining){
        this.handler = handler;
        this.sender = sender;
        this.relationshipDefining = relationshipDefining;
    }

    /**
     * 添加消息处理器
     * @param messageHandler 消息处理器
     * @param type 处理的消息类型
     */
    public void addMessageHandler(MessageHandler messageHandler, String type){
        handler.addMessageHandler(messageHandler, type);
    }

    /**
     * 通过 Key 发送消息
     * @param key 消息 Key
     * @param payload 消息体
     */
    public void sendMessage(String key, WebSocketSendPayload payload){
        sender.sendByKey(key, payload);
    }

    /**
     * 直接使用 Session 发送消息
     * @param session Session
     * @param payload 消息体
     */
    public void sendMessageBySession(WebSocketSession session, WebSocketSendPayload payload){
        sender.sendWebSocket(session, payload);
    }

    /**
     * 关联key,webSocket
     */
    public void contact(WebSocketSession session, String key){
        relationshipDefining.contact(key, session);
    }

    /**
     * 解除key,webSocket的关联
     */
    public void removeKeyContact(WebSocketSession session, String key){
        relationshipDefining.removeKeyContact(session, key);
    }

}
