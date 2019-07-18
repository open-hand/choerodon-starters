package io.choerodon.websocket.v2.handler;

import io.choerodon.websocket.relationship.RelationshipDefining;
import io.choerodon.websocket.v2.receive.MessageHandler;
import io.choerodon.websocket.v2.send.MessageSender;
import io.choerodon.websocket.v2.send.WebSocketSendPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SubReceiveMessageHandler implements MessageHandler<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnSubReceiveMessageHandler.class);

    private RelationshipDefining relationshipDefining;

    private MessageSender messageSender;

    public SubReceiveMessageHandler(RelationshipDefining relationshipDefining, MessageSender messageSender) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String payload) {
        LOGGER.info("webSocket unsub {},session id ={}", key, session.getId());
        if (!StringUtils.isEmpty(key)) {
            relationshipDefining.contact(key, session);
            messageSender.sendWebSocket(session, new WebSocketSendPayload<>(type, key, relationshipDefining.getKeysBySession(session)));
        }
    }

    @Override
    public String matchType() {
        return "sub";
    }
}
