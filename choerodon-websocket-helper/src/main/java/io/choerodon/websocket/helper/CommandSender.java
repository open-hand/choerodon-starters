package io.choerodon.websocket.helper;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.listener.AgentCommandListener;
import io.choerodon.websocket.tool.UUIDTool;

import java.util.UUID;

/**
 *
 */
public class CommandSender {
    private AgentCommandListener agentCommandListener;

    public CommandSender(AgentCommandListener agentCommandListener) {
        this.agentCommandListener = agentCommandListener;
    }

    public void sendMsg(Msg msg){
        msg.setDispatch(true);
        agentCommandListener.onMsg(msg);
    }
}
