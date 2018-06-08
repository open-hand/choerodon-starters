package io.choerodon.websocket.channel;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.SocketRegister;
import io.choerodon.websocket.SocketSender;
import io.choerodon.websocket.websocket.SocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

/**
 * RedisMsgListener
 * @author jiatong.li
 */
public class RedisMsgListener implements MessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMsgListener.class);

    private SocketSender socketSender;
    private SocketRegister socketRegister;
    private SocketProperties socketProperties;
    public RedisMsgListener(SocketSender socketSender, SocketRegister socketRegister, SocketProperties socketProperties) {
        this.socketSender = socketSender;
        this.socketRegister = socketRegister;
        this.socketProperties = socketProperties;
    }


    private JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
    @Override
    public void onMessage(Message message, byte[] pattern) {
        Msg msg = (Msg) serializer.deserialize(message.getBody());
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("receive msg {} from channel {}",msg.getType(),new String(message.getChannel()));
        }
        if (msg.getMsgType() == Msg.COMMAND && msg.getCommandId() != null && socketProperties.isCommandTimeoutEnabled()){
            if (LOGGER.isDebugEnabled()){
                LOGGER.debug("remove command id {}",msg.getCommandId());
            }
            socketRegister.removeCommandSend(msg.getCommandId());
        }
        socketSender.sendToSocket(msg);
    }
}
