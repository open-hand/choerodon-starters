package io.choerodon.websocket.session;

import io.choerodon.websocket.SocketRegister;
import io.choerodon.websocket.listener.SimpleMsgListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSessionListener implements SessionListener {
    public static final Logger logger = LoggerFactory.getLogger(SimpleMsgListener.class);
    private SocketRegister socketRegister;
    private SessionRepository sessionRepository;

    public SimpleSessionListener(SocketRegister socketRegister, SessionRepository sessionRepository) {
        this.socketRegister = socketRegister;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void onConnected(Session session) {
        sessionRepository.add(session.getUuid(),session);
        if(session.getRegisterKey() != null){
            socketRegister.register(session.getRegisterKey(),session.getUuid());
        }
    }

    @Override
    public Session onClose(String sessionId) {
        Session session = sessionRepository.removeById(sessionId);
        //a session must  have key
        socketRegister.unRegisterAll(session.getRegisterKey(),sessionId);
        return session;
    }
}
