package io.choerodon.websocket.security;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.HandshakeFailureException;


/**
 * @author crcokitwood
 */
public interface SecurityInterceptor {

    //security check when create socket connection

    /**
     * 初始化连接检查
     * @param request ServletServerHttpRequest
     * @throws HandshakeFailureException  HandshakeFailureException
     */
    void check(ServletServerHttpRequest request) throws HandshakeFailureException;
}
