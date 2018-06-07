package io.choerodon.websocket.helper;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.listener.InformMsgListener;
/*
消息发送至web前端websocket
 */
public class InformSender {

    private InformMsgListener informMsgListener;
    public InformSender(InformMsgListener informMsgListener) {
        this.informMsgListener = informMsgListener;
    }

    public void  sendMsg(Msg msg){
        informMsgListener.onMsg(msg);
    }
}
