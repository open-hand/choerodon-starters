package io.choerodon.websocket.websocket;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.helper.PathHelper;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.tool.SerializeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * @author jiatong.li
 */
public class SocketHandler extends AbstractWebSocketHandler {
    private static final String SESSION_ID = "SESSION_ID";
    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private SockHandlerDelegate sockHandlerDelegate;
    private PathHelper pathHelper;

    public SocketHandler(SockHandlerDelegate sockHandlerDelegate, PathHelper pathHelper) {
        this.pathHelper = pathHelper;
        this.sockHandlerDelegate = sockHandlerDelegate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sockHandlerDelegate.onSessionCreated(upgradeSession(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info(session +":"+ message.getPayload());
        Msg msg = SerializeTool.readMsg(message.getPayload());
        String sessionId = getSessionId(session);
        int sessionType = pathHelper.getSessionType(session.getUri().getPath());
        if(msg != null){
            switch (sessionType){
                case Session.AGENT:
                    msg.setMsgType(Msg.AGENT);
                    msg.setEnvId((String) session.getAttributes().get("envId"));
                    break;
                case Session.EXEC:
                    msg.setMsgType(Msg.PIPE);
                    break;
                case Session.LOG:
                     msg.setMsgType(Msg.PIPE);
                     break;
                case Session.COMMON:
                    msg.setMsgType(Msg.DEFAULT);
                    break;
                default:
                    msg.setMsgType(Msg.DEFAULT);
                    break;
            }
            msg.setBrokerFrom(sessionId+session.getAttributes().get("key").toString());
            sockHandlerDelegate.onMsgReceived(msg);
        }

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        try{
            CharBuffer charBuffer = null;
            ByteBuffer buffer = message.getPayload();
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            String payload = charBuffer.toString();
            String sessionId = getSessionId(session);
            Msg msg = new Msg();
            msg.setPayload(payload);
            msg.setKey((String) session.getAttributes().get("key"));
            msg.setBrokerFrom(sessionId+msg.getKey());
            msg.setMsgType(Msg.PIPE);
            sockHandlerDelegate.onMsgReceived(msg);
        }catch (Exception e){
            logger.error("handle binary message error!!!!",e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        sockHandlerDelegate.onSessionDisConnected(getSessionId(session));
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
}
