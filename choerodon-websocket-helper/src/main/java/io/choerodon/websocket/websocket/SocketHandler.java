package io.choerodon.websocket.websocket;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.helper.PathHelper;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.session.SessionRepository;
import io.choerodon.websocket.tool.SerializeTool;
import io.choerodon.websocket.websocket.health.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.ByteBuffer;

/**
 * @author jiatong.li
 */
public class SocketHandler extends AbstractWebSocketHandler {
    private static final String SESSION_ID = "SESSION_ID";
    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private SockHandlerDelegate sockHandlerDelegate;
    private PathHelper pathHelper;
    private HealthCheck healthCheck;
    private SessionRepository sessionRepository;

    public SocketHandler(SessionRepository sessionRepository,SockHandlerDelegate sockHandlerDelegate, PathHelper pathHelper, HealthCheck healthCheck) {
        this.pathHelper = pathHelper;
        this.sockHandlerDelegate = sockHandlerDelegate;
        this.healthCheck = healthCheck;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession socketSession) throws Exception {
        Session session = upgradeSession(socketSession);
        sockHandlerDelegate.onSessionCreated(session);

        healthCheck.onCreated(session);
    }

    @Override
    protected void handlePongMessage(WebSocketSession socketSession, PongMessage message) throws Exception {
        doHandlePongPingMessage(socketSession, message);
    }

    /**
     * Extend the old handle and now listen for Ping messages.
     */
    protected void handlePingMessage(WebSocketSession socketSession, PingMessage message) throws Exception {
        doHandlePongPingMessage(socketSession, message);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (PingMessage.class.isInstance(message)) {
            handlePingMessage(session, (PingMessage) message);
        } else {
            super.handleMessage(session, message);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession socketSession, TextMessage message) throws Exception {

        String sessionId = getSessionId(socketSession);
        Session session = sessionRepository.getById(getSessionId(socketSession));
        if (session == null) {
            throw new Exception(String.format("Invalid session information![remote=%s]", socketSession.getRemoteAddress().toString()));
        }

        try {
            int sessionType = pathHelper.getSessionType(socketSession.getUri().getPath());
            int msgType;
            switch (sessionType) {
                case Session.AGENT:
                    msgType = Msg.AGENT;
                    break;
                case Session.EXEC:
                    msgType = Msg.FRONT_PIP_EXEC;
                    break;
                case Session.LOG:
                    msgType = Msg.PIPE;
                    break;
                case Session.COMMON:
                    msgType = Msg.DEFAULT;
                    break;
                default:
                    msgType = Msg.DEFAULT;
                    break;
            }
            Msg msg = null;
            if (msgType == Msg.FRONT_PIP_EXEC) {
                msg = new Msg();
                msg.setPayload(message.getPayload());
                msg.setKey((String) socketSession.getAttributes().get("key"));
            } else {
                msg = SerializeTool.readMsg(message.getPayload());
            }
            msg.setMsgType(msgType);
            logger.info("receive {} msg of {},", msg.getType(), msg.getKey());
            if (msg.getMsgType() == Msg.AGENT) {
                msg.setClusterId((String) socketSession.getAttributes().get("clusterId"));
            }
            msg.setBrokerFrom(sessionId + socketSession.getAttributes().get("key").toString());
            sockHandlerDelegate.onMsgReceived(msg);
        } finally {
            if (session != null) {
                healthCheck.onReceived(session, message);
            }
        }

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession socketSession, BinaryMessage message) throws Exception {
        Session session = sessionRepository.getById(getSessionId(socketSession));

        if (session == null) {
            throw new Exception(String.format("Invalid session information![remote=%s]", socketSession.getRemoteAddress().toString()));
        }

        try{
            Msg msg = new Msg();

            switch (pathHelper.getSessionType(socketSession.getUri().getPath())){
                case Session.EXEC:
                    msg.setMsgType(Msg.PIPE_EXEC);
                    break;
                case Session.LOG:
                    msg.setMsgType(Msg.PIPE);
                    break;
                default:
                    msg.setMsgType(Msg.DEFAULT);
                    break;
            }
            ByteBuffer buffer = message.getPayload();
            byte[] bytesArray = new byte[buffer.remaining()];
            buffer.get(bytesArray, 0, bytesArray.length);
            String sessionId = getSessionId(socketSession);
            if (msg.getMsgType() == Msg.PIPE_EXEC) {
                if (bytesArray[0] == 63 ) {
                    byte[] newByteArray = new byte[bytesArray.length-1];
                    System.arraycopy(bytesArray, 1, newByteArray, 0, newByteArray.length);
                    bytesArray = newByteArray;
                }
                msg.setPayload(new String(bytesArray, "utf-8"));

            } else {
                msg.setBytesPayload(bytesArray);
            }
            msg.setKey((String) socketSession.getAttributes().get("key"));
            msg.setBrokerFrom(sessionId+msg.getKey());
            sockHandlerDelegate.onMsgReceived(msg);
        }catch (Exception e){
            logger.error("handle binary message error!!!!",e);
        } finally {
            if (session != null) {
                healthCheck.onReceived(session, message);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession socketSession, Throwable exception) throws Exception {

        if (socketSession.isOpen()) {
            socketSession.close(CloseStatus.PROTOCOL_ERROR);
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession socketSession, CloseStatus closeStatus) throws Exception {

        Session session = sessionRepository.getById(getSessionId(socketSession));

        sockHandlerDelegate.onSessionDisConnected(getSessionId(socketSession));

        healthCheck.onClosed(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Session upgradeSession(WebSocketSession webSocketSession){
        Session session = new Session(webSocketSession);
        session.setType(pathHelper.getSessionType(webSocketSession.getUri().getPath()));
       return session;
    }

    private String getSessionId(WebSocketSession session){
        return (String) session.getAttributes().get(SESSION_ID);
    }

    private void doHandlePongPingMessage(WebSocketSession socketSession, WebSocketMessage<?> message) throws Exception {
        Session session = sessionRepository.getById(getSessionId(socketSession));

        if (session == null) {
            throw new Exception(String.format("Invalid session information![remote=%s]", socketSession.getRemoteAddress().toString()));
        }

        healthCheck.onReceived(session, message);
    }
}
