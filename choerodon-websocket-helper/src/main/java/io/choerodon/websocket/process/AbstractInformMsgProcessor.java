package io.choerodon.websocket.process;

import io.choerodon.websocket.Msg;

public abstract class AbstractInformMsgProcessor implements MsgProcessor{
    @Override
    public boolean shouldProcess(Msg msg) {
        return msg.getMsgType() == Msg.INFORM;
    }
}

