package io.choerodon.websocket.listener;

import io.choerodon.websocket.Msg;

public abstract class MsgListenerDecorator implements MsgListener{
    private MsgListener msgListener;

    public MsgListenerDecorator(MsgListener msgListener) {
        this.msgListener = msgListener;
    }

    @Override
    public void onMsg(Msg msg) {
        msgListener.onMsg(msg);
    }
}
