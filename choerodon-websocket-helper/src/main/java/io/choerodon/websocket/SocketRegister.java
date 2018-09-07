package io.choerodon.websocket;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


/**
 * @author jiatong.li
 */
public class SocketRegister {
    private RedisTemplate<String,String> stringRedisTemplate;
    private static final String  KEY_PREFIX = "KEY:";
    private static final String SOCKET_PREFIX = "SOCKET:";
    private static final String BROKER_SOCKETS_PREFIX = "brokers:";
    private static final String COMMAND_KEY = "commands";

    public SocketRegister(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void register(String key, String socketId){
        //注册msg key 与socket uuid本socket关心的目标
        stringRedisTemplate.opsForSet().add(KEY_PREFIX+key,socketId+key);
        //注册 socket uuid 与 broker
        stringRedisTemplate.opsForValue().set(SOCKET_PREFIX+socketId+key, SocketHelperAutoConfiguration.BROKER_ID);
        //register brokers sockets
        stringRedisTemplate.opsForSet().add(BROKER_SOCKETS_PREFIX+SocketHelperAutoConfiguration.BROKER_ID,SOCKET_PREFIX+socketId+key);

    }

    public boolean isKeyRegister(String key){
       return stringRedisTemplate.opsForSet().members(KEY_PREFIX+key).size()>0;
    }

    public void unRegisterAll(String key, String socketId){
        //unregister socket
        stringRedisTemplate.delete(SOCKET_PREFIX+socketId+key);
        //unregister key
        stringRedisTemplate.opsForSet().remove(KEY_PREFIX+key,socketId+key);
        //unregister broker sockets
        stringRedisTemplate.opsForSet().remove(BROKER_SOCKETS_PREFIX+SocketHelperAutoConfiguration.BROKER_ID,SOCKET_PREFIX+socketId+key);
    }

    public void unRegisterKey(String msgKey, String socketId){
        stringRedisTemplate.opsForSet().remove(KEY_PREFIX+msgKey,socketId);
    }

    public void registerCommandSend(Long commandId){
        stringRedisTemplate.opsForHash().put(COMMAND_KEY,commandId+"",System.currentTimeMillis()+"");
    }

    public void  removeCommandSend(Long commandId){
        stringRedisTemplate.opsForHash().delete(COMMAND_KEY,String.valueOf(commandId));
    }

}
