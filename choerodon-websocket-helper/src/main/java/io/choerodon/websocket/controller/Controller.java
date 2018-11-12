package io.choerodon.websocket.controller;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.SocketHelperAutoConfiguration;
import io.choerodon.websocket.process.AbstractAgentMsgHandler;
import io.choerodon.websocket.session.AgentOptionListener;
import io.choerodon.websocket.websocket.SocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    //no leader no manage broker
    //public static final String SOCKET_LEADER_KEY ="controller_broker";
    private static final String BROKERS_KEY = "brokers";
    private static final String COMMANDS_KEY = "commands";
    private static final String AGENT_SESSION = "cluster-sessions";
    private static final String COMMAND_TIMEOUT = "command_not_send";
    private RedisTemplate<String,String> stringRedisTemplate;
    private RedisTemplate<Object,Object> redisTemplate;
    private AgentOptionListener agentOptionListener;
    private static final String  KEY_PREFIX = "KEY:";
    private static final String SOCKET_PREFIX = "SOCKET:";
    private static final String BROKER_SOCKETS_PREFIX = "brokers:";
    private static volatile boolean running = true;
    private static final long MAX_DURATION = 30000;
    private SocketProperties socketProperties;
    private AbstractAgentMsgHandler agentMsgHandler;

    public Controller(RedisTemplate<String, String> stringRedisTemplate,
                      RedisTemplate<Object,Object> redisTemplate,
                      AgentOptionListener agentOptionListener,
                      SocketProperties socketProperties,
                      AbstractAgentMsgHandler abstractAgentMsgHandler) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.agentOptionListener = agentOptionListener;
        this.redisTemplate = redisTemplate;
        this.socketProperties = socketProperties;
        this.agentMsgHandler = abstractAgentMsgHandler;

        ScheduledExecutorService register = new ScheduledThreadPoolExecutor(1);
        ScheduledExecutorService cleaner = new ScheduledThreadPoolExecutor(1);
        register.scheduleAtFixedRate(new RegisterThread(),2000,socketProperties.getRegisterInterval(),TimeUnit.MILLISECONDS);
        cleaner.scheduleAtFixedRate(new CleanThread(),2000,socketProperties.getRegisterInterval()+1000,TimeUnit.MILLISECONDS);
        if (socketProperties.isCommandTimeoutEnabled()){
            ScheduledExecutorService commandRecorder = new ScheduledThreadPoolExecutor(1);
            commandRecorder.scheduleAtFixedRate(new CommandRecorderThread(),2,socketProperties.getCommandTimeoutSeconds(),TimeUnit.SECONDS);

        }

    }

    class CleanThread implements Runnable{

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            Map<Object,Object> objectMap =stringRedisTemplate.opsForHash().entries(BROKERS_KEY);
            Map<String, String> brokers = (Map<String,String>)(Map)objectMap;
            //check alive of other brokers
            Set<String> brokerIds = brokers.keySet();
            Set<String> existAgents = new HashSet<>();
            Set<String> envs = (Set<String>)(Set)redisTemplate.opsForHash().entries(AGENT_SESSION).keySet();
            for (String brokerId : brokerIds){
                long brokerLastRenewTime = Long.valueOf(brokers.get(brokerId));
                long duration = now - brokerLastRenewTime;
                if( duration > socketProperties.getDurationCount() * socketProperties.getRegisterInterval()){
                    LOGGER.warn(brokerId+" down !  last renew time is {}, and now is {}, duration {} exceed ",brokerLastRenewTime, now, duration);
                    //清除注册
                    stringRedisTemplate.opsForHash().delete(BROKERS_KEY,brokerId);


                    Set<String> socketIdKeys = stringRedisTemplate.opsForSet().members(BROKER_SOCKETS_PREFIX+brokerId);
                    for (String socketIdkey : socketIdKeys){
                        //清除 socket
                        stringRedisTemplate.delete(socketIdkey);
                        String key = getSocketKey(socketIdkey);

                        //unregister key
                        stringRedisTemplate.opsForSet().remove(KEY_PREFIX+key,socketIdkey.substring(7));

                        //
                        if (envs.contains(key)){
                            LOGGER.info("env "+key+"close----------");
                            agentOptionListener.onClose(key, true);
                        }
                    }
                    //clean broker sockets
                    LOGGER.info("delete broker keys {}", brokerId);
                    stringRedisTemplate.delete(BROKER_SOCKETS_PREFIX+brokerId);
                } else {
                    Set<String> socketIdKeys = stringRedisTemplate.opsForSet().members(BROKER_SOCKETS_PREFIX+brokerId);
                    existAgents.addAll(socketIdKeys);
                }
            }
            Set<String> existKeys =  existAgents.stream().map(Controller.this::getSocketKey).collect(Collectors.toSet());
            for ( String redisAgentKey : envs ) {
                if (!existKeys.contains(redisAgentKey)) {
                    LOGGER.info("delete agent session ", redisAgentKey);
                    agentOptionListener.onClose(redisAgentKey, true);
                }
            }
        }


    }

    class RegisterThread implements Runnable{
        @Override
        public void run() {
                long now = System.currentTimeMillis();
                stringRedisTemplate.opsForHash().put(BROKERS_KEY,SocketHelperAutoConfiguration.BROKER_ID,now+"");
                LOGGER.debug("broker {} register at {} ",SocketHelperAutoConfiguration.BROKER_ID , now );
        }
    }

    class CommandRecorderThread implements Runnable{

        @Override
        public void run() {
                Map<String,String> commands  = (Map<String, String>)(Map) stringRedisTemplate.opsForHash().entries(COMMANDS_KEY);

                long now = System.currentTimeMillis();
                Iterator<Map.Entry<String,String>> iterator = commands.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String,String>  command = iterator.next();
                    long sendTime = Long.parseLong(command.getValue());
                    if(now-sendTime > socketProperties.getCommandTimeoutSeconds()*1000){
                        LOGGER.info("command time out {} ",command.getKey());
                        agentMsgHandler.process(timeoutMsg(Long.parseLong(command.getKey())));
                        stringRedisTemplate.opsForHash().delete(COMMANDS_KEY,command.getKey());
                    }
                }

        }
    }

    private String getSocketKey(String socketIdKey){
        return socketIdKey.substring(39);
    }

    private Msg timeoutMsg(Long commandId){
        Msg msg = new Msg();
        msg.setMsgType(Msg.AGENT);
        msg.setType(COMMAND_TIMEOUT);
        msg.setCommandId(commandId);
        msg.setPayload("send command time out");
        msg.setKey("command:timeout");
        return msg;
    }
}


