package io.choerodon.event.consumer;

/**
 * 用以确保消息不会重复消息的去重器
 * @author zhipeng.zuo
 *      Created on 2017/10/26.
 */
public interface DuplicateRemoveListener {

    /**
     * 判断指定uuid的消息是否已经被消费
     *
     * @param uuid 消息的uuid
     * @return 是否被消费的结果
     */
    boolean hasBeanConsumed(String uuid);

    /**
     * 消息消费成功后执行的操作
     *
     * @param uuid 消息的uuid
     */
    void after(String uuid);

}
