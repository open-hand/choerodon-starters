package io.choerodon.websocket.session;

public interface SessionListener {
    void onConnected(Session session);
    Session onClose(String sessionId);
}
