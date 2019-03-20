/*
 * #{copyright}#
 */

package io.choerodon.message;

/**
 * 
 * @author shengyang.zhou@hand-china.com
 * @param <T>
 *            消息类型
 */
public interface IMessageConsumer<T> {
    String DEFAULT_METHOD_NAME = "onMessage";

    /**
     * 
     * @param message
     *            经过反序列化的消息
     * @param pattern
     *            list name or channel
     */
    void onMessage(T message, String pattern);
}
