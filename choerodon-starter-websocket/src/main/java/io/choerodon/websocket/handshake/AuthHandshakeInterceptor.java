package io.choerodon.websocket.handshake;

import io.choerodon.websocket.ChoerodonWebSocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.WebSocketHandler;

import java.util.List;
import java.util.Map;

public class AuthHandshakeInterceptor implements WebSocketHandshakeInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    private static final RestTemplate restTemplate = new RestTemplate();

    private ChoerodonWebSocketProperties choerodonWebSocketProperties;

    public AuthHandshakeInterceptor(ChoerodonWebSocketProperties choerodonWebSocketProperties) {
        this.choerodonWebSocketProperties = choerodonWebSocketProperties;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler, Map<String, Object> map) {
        try {
            HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
            List<String> accessToken = httpHeaders.get(HttpHeaders.AUTHORIZATION);
            if (accessToken != null && accessToken.size() == 1) {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, accessToken.get(0));
                HttpEntity<String> entity = new HttpEntity<>("", headers);
                ResponseEntity responseEntity = restTemplate.exchange(choerodonWebSocketProperties.getOauthUrl(), HttpMethod.GET, entity, String.class);
                return responseEntity.getStatusCode().is2xxSuccessful();

            } else {
                LOGGER.warn("reject webSocket connect, header must have 'Authorization' access-token");
                return false;
            }

        } catch (RestClientException e) {
            LOGGER.error("reject webSocket connect, redirect request to oauth-server error", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        // do nothing
    }
}
