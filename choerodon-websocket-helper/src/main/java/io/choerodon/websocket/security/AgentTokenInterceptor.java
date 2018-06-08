package io.choerodon.websocket.security;

//check agent token and env
public interface AgentTokenInterceptor {
    boolean checkToken(String envId,String token);
}
