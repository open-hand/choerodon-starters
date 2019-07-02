package io.choerodon.websocket.v2.helper;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Created by hailuo.liu@choerodon.io on 2019/7/2.
 */
public class TokenHandshakeInterceptor implements HandshakeInterceptor {
    private TokenChecker tokenChecker;
    public TokenHandshakeInterceptor(TokenChecker tokenChecker){
        this.tokenChecker = tokenChecker;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, @Nullable Exception e) {

    }
}
