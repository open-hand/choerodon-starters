package io.choerodon.websocket.process;

import io.choerodon.websocket.Msg;

//process msg from agent
public abstract class AgentMsgProcessor implements MsgProcessor {
    protected int order = 50;

    @Override
    public boolean shouldProcess(Msg msg) {
        return msg.getMsgType() == Msg.AGENT;
    }
}
