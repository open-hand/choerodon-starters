package io.choerodon.websocket.helper;

import io.choerodon.websocket.send.relationship.BrokerKeySessionMapper;
import io.choerodon.websocket.receive.MessageHandlerAdapter;
import io.choerodon.websocket.send.MessageSender;
import io.choerodon.websocket.receive.TextMessageHandler;
import io.choerodon.websocket.send.SendMessagePayload;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class WebSocketHelper {
    private MessageSender sender;
    private BrokerKeySessionMapper brokerKeySessionMapper;

    public WebSocketHelper(MessageSender sender, BrokerKeySessionMapper brokerKeySessionMapper){
        this.sender = sender;
        this.brokerKeySessionMapper = brokerKeySessionMapper;
    }

    /**
     * 通过 Key 发送消息
     * @param key 消息 Key
     * @param payload 消息体
     */
    public void sendMessageByKey(String key, SendMessagePayload payload){
        sender.sendByKey(key, payload);
    }

    /**
     * 直接使用 Session 发送消息
     * @param session Session
     * @param payload 消息体
     */
    public void sendMessageBySession(WebSocketSession session, SendMessagePayload payload){
        sender.sendBySession(session, payload);
    }

    /**
     * 关联key,webSocket
     */
    public void subscribe(String key, WebSocketSession session){
        brokerKeySessionMapper.subscribe(key, session);
    }

    /**
     * 解除key,webSocket的关联
     */
    public void unsubscribe(String key, WebSocketSession session){
        brokerKeySessionMapper.subscribe(key, session);
    }

}
