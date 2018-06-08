package io.choerodon.websocket.listener;

import io.choerodon.websocket.Msg;

public class InformMsgListener extends MsgListenerDecorator{
    private MsgListener listener;

    public InformMsgListener(MsgListener msgListener) {
        super(msgListener);
    }

    @Override
    public void onMsg(Msg msg) {
        msg.setMsgType(Msg.INFORM);
        super.onMsg(msg);
    }
}
