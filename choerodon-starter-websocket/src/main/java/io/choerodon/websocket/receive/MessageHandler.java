package io.choerodon.websocket.receive;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.ParameterizedType;

public interface MessageHandler<T> {

    default void handle(WebSocketSession session, String type, String key, T payload){

    }

    default void handle(WebSocketSession session, BinaryMessage message){

    }

    default String matchType() {
        return WebSocketMessageHandler.MATCH_ALL_STRING;
    }

    default String matchPath() {
        return WebSocketMessageHandler.MATCH_ALL_STRING;
    }

    @SuppressWarnings("unchecked")
    default Class<T> payloadClass() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericInterfaces()[0];
        return (Class<T>) type.getActualTypeArguments()[0];
    }

}
