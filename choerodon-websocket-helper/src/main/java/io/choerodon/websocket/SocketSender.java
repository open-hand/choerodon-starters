package io.choerodon.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.websocket.process.AbstractAgentMsgHandler;
import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;


/**
 * @author jiatong.li
 */
public class SocketSender {
    private static final String COMMAND_TIMEOUT = "command_not_send";
    private static final Logger logger = LoggerFactory.getLogger(SocketSender.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private SessionRepository sessionRepository;
    private AbstractAgentMsgHandler agentMsgHandler;

    public SocketSender(SessionRepository sessionRepository,AbstractAgentMsgHandler agentMsgHandler) {
        this.sessionRepository = sessionRepository;
        this.agentMsgHandler = agentMsgHandler;
    }

    public void sendToSocket(Msg msg){
        logger.debug("current brockID: "+SocketHelperAutoConfiguration.BROkER_ID);
        if(msg.getPayload() == null){
            msg.setPayload("");
        }
        String err = null;
        logger.debug("receive msg to send to socket with key :"+msg.getKey()+" and length is: "+msg.getPayload().length());
        boolean isAgentSend = false;
        for (String socketId : msg.getBrokersTO().get(SocketHelperAutoConfiguration.BROkER_ID)){
            try{
                Session session = sessionRepository.getById(socketId);
                if (session == null){
                    logger.error("cant find session when send msg: ",msg);
                    return;
                }else if(msg.getMsgType() == Msg.INTER){
                    session.getWebSocketSession().close();
                    logger.debug("close pipe socket for inter close msg: ",session);
                    return;
                }
                if(session.getType() == Session.AGENT && isAgentSend){
                    //多个agent session只发送一次
                    continue;
                }
                WebSocketSession realSession = session.getWebSocketSession();
                synchronized (realSession) {
                    if (msg.getMsgType() == Msg.PIPE) {
                        BinaryMessage toSend = new BinaryMessage(msg.getBytesPayload());
                        realSession.sendMessage(toSend);
                    } else if (msg.getMsgType() == Msg.PIPE_EXEC) {
                        realSession.sendMessage(new TextMessage(msg.getPayload()));
                    } else if (msg.getMsgType() == Msg.FRONT_PIP_EXEC) {
                        byte[] old = msg.getPayload().getBytes();
                        byte[] bytes = new byte[old.length+1];
                        bytes[0] = 0x0;
                        System.arraycopy(old, 0, bytes, 1, old.length);
                        realSession.sendMessage(new BinaryMessage(bytes));
                    } else {
                        realSession.sendMessage(new TextMessage(OBJECT_MAPPER.writeValueAsString(msg.simpleMsg())));
                    }
                }


                if (session.getType() == Session.AGENT){
                    isAgentSend =true;
                }
                logger.debug("send to socket success: "+socketId);

            } catch (IllegalArgumentException e) {
                err = "Agent session close!";
                logger.error("cant find session when send: ",e);
            }catch (JsonProcessingException e){
                err = "Error msg format";
                logger.error("format msg to json error: ",e);
            }catch (IOException e){
                err = "Agent session close!";
                logger.error("session"+socketId+" disconnected when send msg");
            }catch (IllegalStateException e){
                err = "Send msg IllegalStateException";
                logger.error("send error ",e);
            }
            if(err != null && msg.getMsgType() == Msg.COMMAND){
                Msg errMsg = new Msg();
                errMsg.setKey(msg.getKey());
                errMsg.setType(COMMAND_TIMEOUT);
                errMsg.setDispatch(false);
                errMsg.setPayload(err);
                errMsg.setMsgType(Msg.AGENT);
                agentMsgHandler.process(errMsg);
            }
        }
    }
}
