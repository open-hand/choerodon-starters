package io.choerodon.websocket.listener;

import io.choerodon.websocket.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentCommandListener extends MsgListenerDecorator{
    public static final Logger logger = LoggerFactory.getLogger(AgentCommandListener.class);
    public AgentCommandListener(MsgListener msgListener) {
        super(msgListener);
    }

    @Override
    public void onMsg(Msg msg) {
        msg.setMsgType(Msg.COMMAND);
        super.onMsg(msg);
    }
}
