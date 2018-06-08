package io.choerodon.websocket.listener;

import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.session.SessionListener;

public abstract class AbstractSessionListener implements SessionListener {
    private SessionListener sessionListener;


    public AbstractSessionListener(SessionListener sessionListener){
        this.sessionListener = sessionListener;
    }

    @Override
    public void onConnected(Session session) {
        this.sessionListener.onConnected(session);

    }

    @Override
    public Session onClose(String sessionId) {
       return this.sessionListener.onClose(sessionId);
    }
}
