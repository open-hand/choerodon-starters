package io.choerodon.websocket.security;

import io.choerodon.websocket.SocketRegister;
import io.choerodon.websocket.helper.EnvListener;
import io.choerodon.websocket.helper.EnvSession;
import io.choerodon.websocket.helper.PathHelper;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.websocket.SocketProperties;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.HandshakeFailureException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class AgentSecurityInterceptor implements SecurityInterceptor {
    private AgentTokenInterceptor agentTokenInterceptor;
    private PathHelper pathHelper;
    private SocketRegister socketRegister;
    private SocketProperties socketProperties;
    private EnvListener envListener;

    public AgentSecurityInterceptor(AgentTokenInterceptor agentTokenInterceptor,
                                    PathHelper pathHelper,
                                    SocketRegister socketRegister,
                                    SocketProperties socketProperties,
                                    EnvListener envListener) {
        this.agentTokenInterceptor = agentTokenInterceptor;
        this.pathHelper = pathHelper;
        this.socketRegister = socketRegister;
        this.socketProperties = socketProperties;
        this.envListener = envListener;
    }

    @Override
    public void check(ServletServerHttpRequest request) {
        if (agentTokenInterceptor == null){
            if (socketProperties.isSecurity()){
                throw new RuntimeException("No agent Token check");
            }
        }else {
            String envId = request.getServletRequest().getParameter("clusterId");
            String token = request.getServletRequest().getParameter("token");
            String version = request.getServletRequest().getParameter("version");
            if ((envId == null ||
                    envId.trim().isEmpty()) ||
                    (token == null || token.trim().isEmpty()) ||
                    version == null || version.trim().isEmpty()){
                throw new RuntimeException("envId or token or version null!");
            }
            boolean success = agentTokenInterceptor.checkToken(request.getServletRequest().getParameter("envId"),
                    request.getServletRequest().getParameter("token"));
            if(!success){
                throw new HandshakeFailureException("agent token check failed");
            }
        }
        if(pathHelper.getSessionType(request.getURI().getPath()) == Session.AGENT){
            String key = request.getServletRequest().getParameter("key");
            if (isEnvAlreadyExist(key)){
                throw new HandshakeFailureException("already have a agent in this env");
            }
        }
    }

    private boolean isEnvAlreadyExist(String key){
        Map<String, EnvSession> maps = (Map<String, EnvSession>) (Map)envListener.connectedEnv();
        Set<EnvSession> sessions = new HashSet<>(maps.values());
        for (EnvSession session : sessions){
            if (session.getRegisterKey() .equals(key)){
                return true;
            }
        }
        return false;

    }
}
