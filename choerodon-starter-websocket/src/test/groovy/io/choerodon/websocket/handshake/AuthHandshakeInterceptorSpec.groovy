package io.choerodon.websocket.handshake

import io.choerodon.websocket.ChoerodonWebSocketProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class AuthHandshakeInterceptorSpec extends Specification {
    private ChoerodonWebSocketProperties choerodonWebSocketProperties = new ChoerodonWebSocketProperties()
    private AuthHandshakeInterceptor authHandshakeInterceptor = new AuthHandshakeInterceptor(choerodonWebSocketProperties)

    def "BeforeHandshake"() {
        given: "构造请求参数"
        ServerHttpRequest serverHttpRequest = Mock(ServerHttpRequest)
        ServerHttpResponse serverHttpResponse = Mock(ServerHttpResponse)
        WebSocketHandler webSocketHandler = Mock(WebSocketHandler)
        Map<String, Object> map = new HashMap<>()
        HttpHeaders httpHeaders = new HttpHeaders()
        List<String> accessToken = new ArrayList<>()
        accessToken.add("token")
        httpHeaders.put(HttpHeaders.AUTHORIZATION, accessToken)

        when: "调用方法"
        authHandshakeInterceptor.beforeHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map)
        then: "校验参数"
        1 * serverHttpRequest.getHeaders() >> httpHeaders

        when: "调用方法"
        httpHeaders.put(HttpHeaders.AUTHORIZATION, new ArrayList<String>())
        authHandshakeInterceptor.beforeHandshake(serverHttpRequest, serverHttpResponse, webSocketHandler, map)
        then: "校验参数"
        1 * serverHttpRequest.getHeaders() >> httpHeaders
    }
}
