package io.choerodon.websocket.process;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.RedisRouter;
import io.choerodon.websocket.RedisSender;

import java.util.Set;

/**
 * @author jiatong.li
 */
public class SocketMsgDispatcher{

    private RedisRouter redisRouter;
    private RedisSender redisSender;

    public SocketMsgDispatcher(RedisRouter redisRouter, RedisSender redisSender) {
        this.redisRouter = redisRouter;
        this.redisSender = redisSender;
    }

    public void dispatcher(Msg msg){
       Set<String> channels = redisRouter.getToChannel(msg);
       redisSender.sendToChannel(channels,msg);
    }
}
