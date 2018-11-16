package io.choerodon.websocket.session;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.helper.PipeRequest;
import io.choerodon.websocket.listener.AbstractSessionListener;
import io.choerodon.websocket.listener.AgentCommandListener;
import io.choerodon.websocket.process.SocketMsgDispatcher;
import io.choerodon.websocket.tool.MsgFactory;

public class PipeSessionListener extends AbstractSessionListener{
    private static final Logger logger = LoggerFactory.getLogger(InMemorySessionRepository.class);
    private AgentCommandListener agentCommandListener;
    private SocketMsgDispatcher dispatcher;

    public PipeSessionListener(SessionListener sessionListener, AgentCommandListener agentCommandListener , SocketMsgDispatcher dispatcher) {
        super(sessionListener);
        this.agentCommandListener = agentCommandListener;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onConnected(Session session) {
        super.onConnected(session);
        //agent 与 devops 服务建立的 log 连接
        if(session.getWebSocketSession().getUri().getPath().contains("/agent")){
            return;
        }
        PipeRequest pipeRequest = extractLogRequest(session.getWebSocketSession().getAttributes());
        if (pipeRequest == null){
            logger.error("received web log connect but not enable log request , close connection");
            try {
                session.getWebSocketSession().close();
                return;
            } catch (IOException e) {
                logger.info("error when close socket");
            }
        }

        Msg msg = null;

        if (session.getType() == Session.LOG) {
            msg = MsgFactory.logMsg(session.getUuid(),session.getRegisterKey(), pipeRequest);
        } else if (session.getType() == Session.EXEC ) {
            msg = MsgFactory.execMsg(session.getUuid(),session.getRegisterKey(), pipeRequest);
        }
        agentCommandListener.onMsg(msg);
    }

    @Override
    public Session onClose(String sessionId) {
        Session session = super.onClose(sessionId);
        dispatcher.dispatcher(MsgFactory.closeMsg(session));
        return session;
    }

    private PipeRequest extractLogRequest(Map<String,Object> paras){
        String podName = (String) paras.get("podName");
        String containerName = (String) paras.get("containerName");
        String logId = (String) paras.get("logId");
        String namespace = (String) paras.get("env");
        if(podName != null && containerName != null && logId !=null){
            return new PipeRequest(podName,containerName,logId, namespace);
        }else {
            return null;
        }
    }
}
