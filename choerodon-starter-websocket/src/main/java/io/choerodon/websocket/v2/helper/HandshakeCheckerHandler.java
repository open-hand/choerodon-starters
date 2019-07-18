package io.choerodon.websocket.v2.helper;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class HandshakeCheckerHandler implements HandshakeHandler {

    private SocketHandlerRegistration registration;

    public HandshakeCheckerHandler(SocketHandlerRegistration registration){
        this.registration = registration;
    }


    @Override
    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
        return registration.doHandshake(request, response);
    }
}
