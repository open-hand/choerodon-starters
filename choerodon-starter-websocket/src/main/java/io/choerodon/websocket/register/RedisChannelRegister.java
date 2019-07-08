package io.choerodon.websocket.register;

import java.util.Set;

public interface RedisChannelRegister {

    /**
     * 获取存活的channel列表
     */
    Set<String> getSurvivalChannels();

    /**
     * 启动时注册channel
     */
    void registerByChannelName();

    /**
     * 移除不存活的实例对应的channel
     */
    void removeDeathChannel(String channel);

    /**
     * 获取本实例对应的channel名
     */
    String channelName();

}
