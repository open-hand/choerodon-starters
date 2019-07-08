package io.choerodon.websocket.send;

public class WebSocketSendPayload<T> {

    public static final String MSG_TYPE_SESSION = "session";

    private String type;

    private String key;

    private T data;

    public WebSocketSendPayload() {
    }

    public WebSocketSendPayload(String type, String key, T data) {
        this.type = type;
        this.key = key;
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
        return "WebSocketSendPayload{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", data=" + data +
                '}';
    }


}
