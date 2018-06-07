package io.choerodon.websocket.process;


import io.choerodon.websocket.*;
import io.choerodon.websocket.websocket.SocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessManager {
    private static final String COMMAND_TIMEOUT = "command_not_send";
    private final List<MsgProcessor> processors;
    private RedisRouter redisRouter;
    private RedisSender redisSender;
    private SocketSender socketSender;
    private SocketRegister socketRegister;
    private SocketProperties socketProperties;
    public static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
    public ProcessManager(List<MsgProcessor> processors,
                          RedisRouter redisRouter,
                          RedisSender redisSender,
                          SocketSender socketSender,
                          SocketRegister socketRegister,
                          SocketProperties socketProperties){
        this.redisRouter = redisRouter;
        this.redisSender = redisSender;
        this.processors = processors;
        this.socketSender = socketSender;
        this.socketRegister = socketRegister;
        this.socketProperties = socketProperties;
    }

    public void process(Msg msg){
        //first process
        processInter(msg);
        //last
        if(msg.isDispatch()){
            try {
                //根据路由表获取channel
                Set<String> channels = redisRouter.getToChannel(msg);
                if(msg.getMsgType() == Msg.COMMAND &&
                        channels.isEmpty() ){
                   processInter(errorMsg(msg));
                }
                if(channels.contains(SocketHelperAutoConfiguration.BROkER_ID) ){
                    Map<String,Set<String>> localToMap = new HashMap<>();
                    localToMap.put(SocketHelperAutoConfiguration.BROkER_ID,msg.getBrokersTO().get(SocketHelperAutoConfiguration.BROkER_ID));
                    Msg localMsg = new Msg();
                    BeanUtils.copyProperties(msg,localMsg);
                    localMsg.setBrokersTO(localToMap);
                    socketSender.sendToSocket(localMsg);
                    msg.getBrokersTO().remove(SocketHelperAutoConfiguration.BROkER_ID);
                if(msg.getBrokersTO().isEmpty()){
                    return;
                }
            }


                if (channels.isEmpty()){
                    return;
                }
                redisSender.sendToChannel(channels,msg);
                logger.debug("send to msg "+msg.getType()+" to channels :"+channels);
                if (msg.getMsgType() == Msg.COMMAND && msg.getCommandId() != null && socketProperties.isCommandTimeoutEnabled()){
                    socketRegister.registerCommandSend(msg.getCommandId());
                }
                //command 只能有agent关心.不能有其他session关心
                //如果需要不只有agent关心则需要表示是否已经被发送至agent关心的channel

            }catch (Exception e){
                logger.error("dispatch error");
                if(msg.getMsgType() == Msg.COMMAND ){
                    processInter(errorMsg(msg));
                }
            }
        }
    }

    private void processInter(Msg msg){
        for (MsgProcessor msgProcessor : processors){
            if(msgProcessor.shouldProcess(msg)){
                try {
                    msgProcessor.process(msg);
                }catch (Exception e){
                    logger.error("process msg error ",e);
                }
            }
        }
    }

    private Msg errorMsg(Msg msg){
        Msg errorMsg = new Msg();
        errorMsg.setMsgType(Msg.AGENT);
        errorMsg.setDispatch(false);
        errorMsg.setCommandId(msg.getCommandId());
        errorMsg.setKey(msg.getKey());
        errorMsg.setType(COMMAND_TIMEOUT);
        errorMsg.setPayload("Send command failed, agent session closed!");
        return errorMsg;
    }


}
