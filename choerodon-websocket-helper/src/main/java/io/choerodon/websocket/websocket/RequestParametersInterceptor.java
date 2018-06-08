package io.choerodon.websocket.websocket;

import io.choerodon.websocket.security.SecurityCheckManager;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

/**
 * @author jiatong.li
 */
public class RequestParametersInterceptor extends HttpSessionHandshakeInterceptor {
    private static final String SESSION_ID = "SESSION_ID";

    private SecurityCheckManager securityCheckManager;


    public RequestParametersInterceptor(SecurityCheckManager securityCheckManager) {
        super();
        this.securityCheckManager = securityCheckManager;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        super.beforeHandshake(serverHttpRequest, serverHttpResponse, wsHandler, attributes);
        HttpSession httpSession = getRequestSession(serverHttpRequest);
        if (httpSession == null) {
            throw new RuntimeException("Can not get HttpSession");
        }
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
        HttpServletRequest request = servletRequest.getServletRequest();
        request.getParameterMap().forEach(
                (k, v) -> {
                    if (v.length == 1) {
                        String t = v[0];
                        attributes.put(k, t);
                    } else {
                        attributes.put(k, v);
                    }
                }
        );
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        attributes.put(SESSION_ID,uuid);
        securityCheckManager.check(servletRequest);
        return true;
    }

    private HttpSession getRequestSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(true);
        }
        return null;
    }
}
