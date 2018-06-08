package io.choerodon.websocket.session;

import java.util.ArrayList;
import java.util.List;

/**
 * 我们只要维护main session也就是说main session 是需要心跳维持的
 * @author crock
 */
public class AgentSessionManager {
    private List<SessionListener> listeners = new ArrayList<>();
    void onAgentCreated(Session session){
        for (SessionListener agentSessionListener : listeners){
            agentSessionListener.onConnected(session);
        }
    }

    public void addListener(SessionListener sessionListener){
        listeners.add(sessionListener);
    }

    void onAgentClose(Session session){
        for (SessionListener agentSessionListener : listeners){
            agentSessionListener.onClose(session.getUuid());
        }
    }
}
