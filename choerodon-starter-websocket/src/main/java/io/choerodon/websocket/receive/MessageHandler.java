package io.choerodon.websocket.receive;

import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.ParameterizedType;

public interface MessageHandler<T> {

    void handle(WebSocketSession session, String type, String key, T payload);

    default String matchType() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default Class<T> payloadClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericInterfaces()[0];
        return (Class<T>) type.getActualTypeArguments()[0];
    }

}
