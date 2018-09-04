package io.choerodon.websocket;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jiatong.li
 */
public class RedisRouter {

    private static final String KEY_SPLIT_CH = ".";
    private static final String  KEY_PREFIX = "KEY:";
    private static final String SOCKET_PREFIX = "SOCKET:";
    private RedisTemplate<String,String> stringRedisTemplate;
    private SocketRegister socketRegister;

    public RedisRouter(RedisTemplate<String, String> stringRedisTemplate, SocketRegister socketRegister) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.socketRegister = socketRegister;
    }

    public Set<String> getToChannel(Msg msg){
        Set<String> channels = new HashSet<>();
        Set<String> socketIdAndKeys = getToSocketWithKeys(KEY_PREFIX+msg.getKey(),msg.getBrokerFrom(),
                (msg.getMsgType() == Msg.PIPE) || (msg.getMsgType() == Msg.INTER) || (msg.getMsgType() == Msg.FRONT_PIP_EXEC) || (msg.getMsgType() == Msg.PIPE_EXEC));
        if(!socketIdAndKeys.isEmpty()){
            for (String socketIdKey : socketIdAndKeys){

                String channel = stringRedisTemplate.opsForValue().get(SOCKET_PREFIX+socketIdKey);
                if(channel != null){
                    msg.addBrokerSocket(channel,socketIdKey.substring(0,32));
                    channels.add(channel);
                }else {
                    //如果通过socket找不到channel
                    socketRegister.unRegisterKey(msg.getKey(),socketIdKey);
                }
            }
        }
        return channels;
    }

    public Set<String> getToSocketWithKeys(String key,String except,boolean fullMatch){
        Set<String> socketIdWithKeys  = stringRedisTemplate.opsForSet().members(key);
        if(fullMatch){
            if(except!=null){
                socketIdWithKeys.remove(except);
            }
            return socketIdWithKeys;
        }
        //根据key前缀查抄路由
        String temp = key;
        int index;
        int lastLength = 0;
        Set<String> tempSockets;
        for (index = temp.indexOf(KEY_SPLIT_CH);index!=-1;index = temp.indexOf(KEY_SPLIT_CH)){
            String tempKey = key.substring(0,index+lastLength);
            lastLength = index;
            temp = temp.substring(lastLength+1);
            lastLength += 1;
            tempSockets = stringRedisTemplate.opsForSet().members(tempKey);
            if (!tempSockets.isEmpty()){
                socketIdWithKeys.addAll(tempSockets);
            }
        }
        if(except!=null){
           socketIdWithKeys.remove(except);
        }
        return socketIdWithKeys;
    }
}
