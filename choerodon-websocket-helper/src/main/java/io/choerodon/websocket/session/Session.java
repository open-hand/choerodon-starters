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

    /**
     * Healthcheck try number, set zero if success.
     */
    private volatile int healthCheckTriedTimes;

    /**
     * Tecord reccive message time, include heatbeat message.
     */
    private volatile long lastReceive;

    /**
     * Being checked for health.
     */
    private volatile boolean healthChecking;



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

    public boolean isHealthChecking() {
        return healthChecking;
    }

    public void setHealthChecking(boolean healthChecking) {
        this.healthChecking = healthChecking;
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
            "remote=" + (webSocketSession != null ? webSocketSession.getRemoteAddress().toString() : "") +
            ", uuid='" + uuid + '\'' +
            ", pingEnable=" + pingEnable +
            ", type=" + type +
            ", registerKey='" + registerKey + '\'' +
            ", lastPong=" + lastPong +
            ", lastPing=" + lastPing +
            ", healthCheckTriedTimes=" + healthCheckTriedTimes +
            ", lastReceive=" + lastReceive +
            ", healthChecking=" + healthChecking +
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
