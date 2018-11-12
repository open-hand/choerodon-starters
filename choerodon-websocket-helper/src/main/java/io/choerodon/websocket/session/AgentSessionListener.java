package io.choerodon.websocket.session;

import io.choerodon.websocket.helper.EnvSession;
import io.choerodon.websocket.listener.AbstractSessionListener;
import io.choerodon.websocket.listener.OptionalListener;
import io.choerodon.websocket.tool.KeyParseTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentSessionListener extends AbstractSessionListener {
    private OptionalListener optionalListener;
    private static final Logger logger = LoggerFactory.getLogger(AgentSessionListener.class);
    private SessionRepository sessionRepository;
    private AgentSessionManager agentSessionManager;
    public AgentSessionListener(SessionListener sessionListener, SessionRepository sessionRepository,
                                OptionalListener optionalListener,
                                AgentSessionManager agentSessionManager) {
        super(sessionListener);
        this.optionalListener = optionalListener;
        this.sessionRepository = sessionRepository;
        this.agentSessionManager = agentSessionManager;
    }

    @Override
    public void onConnected(Session session) {
        super.onConnected(session);
        logger.debug("agent session created :" +session);
        //新建一个agent session 额外做一些操作
        EnvSession envSession = new EnvSession();
        envSession.setClusterId(Long.parseLong((String) session.getWebSocketSession().getAttributes().get("clusterId")));
        envSession.setVersion((String) session.getWebSocketSession().getAttributes().get("version"));
        envSession.setRegisterKey(session.getRegisterKey());
        optionalListener.onConn(envSession);
        agentSessionManager.onAgentCreated(session);

    }

    @Override
    public Session onClose(String sessionId) {
        Session session = super.onClose(sessionId);
        optionalListener.onClose(session.getRegisterKey(), false);
        logger.info("agent session close "  + sessionId  +"\nthe count of executor :"+sessionRepository.allExecutors().size());
        return session;
    }
}
