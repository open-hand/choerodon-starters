package io.choerodon.message.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author xiangyu.qi@hand-china.com on 2017/9/25.
 */
@Component
public class ChannelAndQueuePrefix {

    private static String CHANNEL_PREFIX;

    public static String addPrefix(String str){
        if(CHANNEL_PREFIX != null && !CHANNEL_PREFIX.isEmpty()){
            return CHANNEL_PREFIX +"."+ str;
        }
        return str;
    }

    public static String removePrefix(String str){
        if(CHANNEL_PREFIX != null && !CHANNEL_PREFIX.isEmpty()){
            return str.replaceFirst(CHANNEL_PREFIX+".","");
        }
        return str;
    }

    @Value("${redis.topic.channel.prefix:}")
    public void setChannelPrefix(String prefix){
        CHANNEL_PREFIX = prefix;
    }

}
