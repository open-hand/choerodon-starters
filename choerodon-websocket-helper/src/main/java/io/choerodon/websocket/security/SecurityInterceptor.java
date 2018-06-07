package io.choerodon.websocket.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpSession;

public interface SecurityInterceptor {

    //security check when create socket connection
    void check(ServletServerHttpRequest request) throws Exception;
}
