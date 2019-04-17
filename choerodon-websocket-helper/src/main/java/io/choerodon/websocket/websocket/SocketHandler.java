package io.choerodon.websocket.websocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        String sessionId = getSessionId(session);
        int sessionType = pathHelper.getSessionType(session.getUri().getPath());
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
            msg.setKey((String) session.getAttributes().get("key"));
        } else {
            msg = SerializeTool.readMsg(message.getPayload());
        }
        msg.setMsgType(msgType);
        logger.info("receive {} msg of {},", msg.getType(), msg.getKey());
        if (msg.getMsgType() == Msg.AGENT) {
            msg.setClusterId((String) session.getAttributes().get("clusterId"));
        }
        msg.setBrokerFrom(sessionId + session.getAttributes().get("key").toString());
        sockHandlerDelegate.onMsgReceived(msg);

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        try {
            Msg msg = new Msg();

            switch (pathHelper.getSessionType(session.getUri().getPath())) {
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
            String sessionId = getSessionId(session);
            if (msg.getMsgType() == Msg.PIPE_EXEC) {
                msg.setPayload(replaceR(new String(bytesArray, StandardCharsets.UTF_8),"",0));
            } else {
                msg.setBytesPayload(bytesArray);
            }
            msg.setKey((String) session.getAttributes().get("key"));
            msg.setBrokerFrom(sessionId + msg.getKey());
            sockHandlerDelegate.onMsgReceived(msg);
        } catch (Exception e) {
            logger.error("handle binary message error!!!!", e);
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

    private Session upgradeSession(WebSocketSession webSocketSession) {
        Session session = new Session(webSocketSession);
        session.setType(pathHelper.getSessionType(webSocketSession.getUri().getPath()));
        return session;
    }

    private String getSessionId(WebSocketSession session) {
        return (String) session.getAttributes().get(SESSION_ID);
    }




    private  String replaceR(String a, String result, int index) {
        if (index >= a.lastIndexOf("\r")) {
            if (a.substring(index, index + 1).equals("\n")) {
                result += a.substring(index - 1);
            } else {
                if (index == 0) {
                    result += a.substring(index + 1);
                }
            }
            return result;
        } else {
            int indexResult = a.indexOf("\r", index);
            if (indexResult != a.length() - 1) {
                String r = a.substring(indexResult + 1, indexResult + 2);
                if (!r.equals("\n")) {
                    result += a.substring(index, indexResult);
                    return replaceR(a, result, indexResult + 1);
                } else {
                    if (!a.substring(index, index + 1).equals("\n")) {
                        result += a.substring(index, indexResult);
                    } else {
                        result += a.substring(index - 1, indexResult);
                    }
                    return replaceR(a, result, indexResult + 1);
                }
            } else {
                if (a.substring(index, index + 1).equals("\n")) {
                    result += a.substring(index - 1, indexResult);
                } else {
                    result += a.substring(index, indexResult);
                }
            }
        }
        return result;
    }
}
