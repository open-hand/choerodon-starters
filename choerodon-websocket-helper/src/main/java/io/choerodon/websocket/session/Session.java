package io.choerodon.websocket.session;

import org.springframework.web.socket.WebSocketSession;

/**
 * @author jiatong.li
 */
public class Session {

    public static final int COMMON = 0;
    public static final int AGENT = 1;
    public static final int LOG = 2;
    public static final int EXEC = 3;
    private static final String HTTP_SESSION_ID_ATTR_NAME = "SESSION_ID";
    private WebSocketSession webSocketSession;
    private final String uuid ;
    private boolean pingEnable;
    private int type;
    private String registerKey;
    private volatile long  lastPong;
    private volatile long  lastPing;

    public Session(WebSocketSession webSocketSession){
        this(webSocketSession,false);
    }

    public Session(WebSocketSession webSocketSession, boolean pingEnable) {
        this.uuid = (String) webSocketSession.getAttributes().get(HTTP_SESSION_ID_ATTR_NAME);
        this.webSocketSession = webSocketSession;
        this.pingEnable = pingEnable;
        String key = (String) webSocketSession.getAttributes().get("key");
        if(key != null){
            this.registerKey = key;
        }
    }

    public boolean isPingEnable() {
        return pingEnable;
    }

    public void setPingEnable(boolean pingEnable) {
        this.pingEnable = pingEnable;
    }

    public long getLastPong() {
        return lastPong;
    }

    public void setLastPong(long lastPong) {
        this.lastPong = lastPong;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRegisterKey() {
        return registerKey;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Session{" +
                "webSocketSession=" + webSocketSession +
                ", uuid='" + uuid + '\'' +
                ", pingEnable=" + pingEnable +
                ", type=" + type +
                ", registerKey='" + registerKey + '\'' +
                ", lastPong=" + lastPong +
                ", lastPing=" + lastPing +
                '}';
    }

    public void setType(int type) {
        this.type = type;
    }
}
