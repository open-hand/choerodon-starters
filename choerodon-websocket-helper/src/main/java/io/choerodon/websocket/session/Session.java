package io.choerodon.websocket.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

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
    private final String uuid;
    private boolean pingEnable;
    private int type;
    private String registerKey;
    /**
     * No use, but keep.
     */
    private volatile long lastPong;
    private volatile long lastPing;

    // cache line padding
    private long a1,a2,a3,a4,a5,a6,a7;
    /**
     * Healthcheck try number, set zero if success.
     */
    private volatile int healthCheckTriedTimes;

    // cache line padding
    private long b1,b2,b3,b4,b5,b6,b7;
    /**
     * Tecord reccive message time, include heatbeat message.
     */
    private volatile long lastReceive;

    // cache line padding
    private long p1,p2,p3,p4,p5,p6,p7;


    public Session(WebSocketSession webSocketSession) {
        this(webSocketSession, false);
    }

    public Session(WebSocketSession webSocketSession, boolean pingEnable) {
        this.uuid = (String) webSocketSession.getAttributes().get(HTTP_SESSION_ID_ATTR_NAME);
        this.webSocketSession = webSocketSession;
        this.pingEnable = pingEnable;
        String key = (String) webSocketSession.getAttributes().get("key");
        if (key != null && !key.isEmpty()) {
            this.registerKey = key;
        } else {
            throw new RuntimeException("Can not found register key!");
        }

        this.lastPing = System.currentTimeMillis();
        this.lastPong = System.currentTimeMillis();
        this.lastReceive = System.currentTimeMillis();
        this.healthCheckTriedTimes = 0;
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

    public long getLastReceive() {
        return lastReceive;
    }

    public void setLastReceive(long lastReceive) {
        this.lastReceive = lastReceive;
    }

    public int getHealthCheckTriedTimes() {
        return healthCheckTriedTimes;
    }

    public void setHealthCheckTriedTimes(int healthCheckTriedTimes) {
        this.healthCheckTriedTimes = healthCheckTriedTimes;
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
            ", lastReceive=" + lastReceive +
            ", healthCheckTriedTimes=" + healthCheckTriedTimes +
            '}';
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Session)) {
            return false;
        }
        Session session = (Session) o;
        return Objects.equals(getUuid(), session.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }
}
