package io.choerodon.event.consumer.rocketmq;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * rocketmq消息处理器
 * @author flyleft
 * 2017/10/23
 */
public interface MessageProcessor {
    /**
     * 处理消息的接口
     *
     * @param messageExt 消息体
     * @return 处理是否成功
     */
    boolean handleMessage(MessageExt messageExt);
}
