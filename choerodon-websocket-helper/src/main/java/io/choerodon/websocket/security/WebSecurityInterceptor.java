package io.choerodon.websocket.security;

import io.choerodon.websocket.websocket.SocketProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.server.HandshakeFailureException;

public class WebSecurityInterceptor implements SecurityInterceptor {
    private SocketProperties socketProperties;
    private static final RestTemplate restTemplate = new RestTemplate();

    public WebSecurityInterceptor(SocketProperties socketProperties) {
        this.socketProperties = socketProperties;
    }

    @Override
    public void check(ServletServerHttpRequest request) throws HandshakeFailureException {
        String token = request.getServletRequest().getParameter("token");
        if (socketProperties.isSecurity()){
            if(token == null || token.trim().isEmpty()){
                throw new RuntimeException("Need Auth 401");
            }
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION,"bearer "+token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            try {
                ResponseEntity responseEntity = restTemplate.exchange(socketProperties.getOauthUrl(), HttpMethod.GET,entity,String.class);
                if(!responseEntity.getStatusCode().is2xxSuccessful()){
                    throw new RuntimeException("auth error");
                }
            }catch (RestClientException e){
                System.out.println("request error");
                throw e;
            }
        }

    }
}
