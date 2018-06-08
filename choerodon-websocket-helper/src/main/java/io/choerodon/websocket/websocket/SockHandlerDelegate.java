package io.choerodon.websocket.websocket;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.listener.MsgListener;
import io.choerodon.websocket.session.SessionListenerFactory;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.session.SessionRepository;

public class SockHandlerDelegate{

    private MsgListener msgListener;
    private SessionListenerFactory sessionListenerFactory;
    private SessionRepository sessionRepository;

    public SockHandlerDelegate(MsgListener msgListener, SessionListenerFactory sessionListenerFactory, SessionRepository sessionRepository) {
        this.msgListener = msgListener;
        this.sessionListenerFactory = sessionListenerFactory;
        this.sessionRepository = sessionRepository;
    }

    public void onSessionCreated(Session session){
        sessionListenerFactory.sessionListener(session.getType())
                .onConnected(session);
    }

    public void onMsgReceived(Msg msg){
        msgListener.onMsg(msg);
    }

    public void onSessionDisConnected(String sessionId){
        Session session = sessionRepository.getById(sessionId);
        sessionListenerFactory.sessionListener(session.getType()).onClose(sessionId);
    }
}
