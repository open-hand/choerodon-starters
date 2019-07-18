package io.choerodon.websocket.v2.receive;

import java.util.Objects;

public class WebSocketReceivePayload<T> {

    private String type;
    private String key;

    private T data;

    public WebSocketReceivePayload() {
    }

    public WebSocketReceivePayload(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "WebSocketReceivePayload{" +
                "type='" + type + '\'' +
                "key='" + key + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketReceivePayload<?> that = (WebSocketReceivePayload<?>) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, data);
    }
}
