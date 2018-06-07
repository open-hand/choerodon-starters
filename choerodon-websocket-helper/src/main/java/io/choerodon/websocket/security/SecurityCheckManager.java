package io.choerodon.websocket.security;

import io.choerodon.websocket.helper.PathHelper;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.tool.KeyParseTool;
import org.springframework.http.server.ServletServerHttpRequest;

public class SecurityCheckManager {
    private PathHelper pathHelper;
    private WebSecurityInterceptor webSecurityInterceptor;
    private AgentSecurityInterceptor agentSecurityInterceptor;

    public SecurityCheckManager(PathHelper pathHelper,
                                WebSecurityInterceptor webSecurityInterceptor,
                                AgentSecurityInterceptor agentSecurityInterceptor) {
        this.pathHelper = pathHelper;
        this.webSecurityInterceptor = webSecurityInterceptor;
        this.agentSecurityInterceptor = agentSecurityInterceptor;
    }

    public void check(ServletServerHttpRequest request) throws Exception{
        String key = request.getServletRequest().getParameter("key");
        if(key == null || key.trim().isEmpty()){
            throw new RuntimeException("Key null");
        }
        if(!KeyParseTool.matchPattern(key)){
            throw new RuntimeException("Key not match the pattern");
        }
        getInterceptor(request.getURI().getPath()).check(request);
    }

    private SecurityInterceptor getInterceptor(String path){
        if(pathHelper.getSessionType(path) == Session.AGENT){
            return agentSecurityInterceptor;
        }else if (pathHelper.getSessionType(path) == Session.COMMON){
            return webSecurityInterceptor;
        }else{
            return new SecurityInterceptor() {
                @Override
                public void check(ServletServerHttpRequest request) throws Exception {

                }
            };
        }

    }
}
