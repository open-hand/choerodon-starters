package io.choerodon.websocket.v2.helper;

import io.choerodon.websocket.v2.receive.MessageHandler;
import io.choerodon.websocket.v2.send.SendHelper;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class WebsocketHelper {
    private WebSocketHandlerRegistry registry;
    private SendHelper sendHelper;
    public WebsocketHelper(WebSocketHandlerRegistry registry){
        this.sendHelper = new SendHelper();
        sendHelper.init();
        this.registry = registry;
    }

    //启用websocket连接路径
    public void addSocketHandler(String path,TokenChecker tokenChecker){
        registry.addHandler(new CommonSocketHandler(this), path).addInterceptors(new TokenHandshakeInterceptor(tokenChecker));
    }

    //添加消息处理器
    public void addMessageHandler(MessageHandler messageHandler,String key){

    }
    //批量添加消息处理
    public void addMessageHandler(MessageHandler messageHandler,List<String> keys){

    }
    //发送消息
    public void sendMessage(String key){

    }
    //直接使用seesionId发送
    public void sendMessageBySession(String webSocketSessionId){

    }


}
