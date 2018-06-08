package io.choerodon.websocket.process;


import io.choerodon.websocket.Msg;


//process msg of command type
public abstract class AbstractCmdMsgHandler implements MsgProcessor {
    int order = 50;

    @Override
    public boolean shouldProcess(Msg msg) {
        return msg.getMsgType()==Msg.COMMAND;
    }
}
