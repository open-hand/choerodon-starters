package io.choerodon.websocket.session;

/**
 * @author crock
 */
public interface AgentConfigurer {
    /**
     * @param agentSessionManager session listener
     */
    void registerSessionListener(AgentSessionManager agentSessionManager);

}
