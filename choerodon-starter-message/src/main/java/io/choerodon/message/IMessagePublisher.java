/*
 * #{copyright}#
 */

package io.choerodon.message;

/**
 * @author shengyang.zhou@hand-china.com
 * @author njq.niu@hand-china.com
 */
public interface IMessagePublisher {

    /**
     * publish message to a channel.
     * 
     * @param channel
     *            channel
     * @param message
     *            message :String ,Number, Map, Object...
     */
    void publish(String channel, Object message);

    /**
     * add message to a queue .
     * 
     * @param list
     *            destination
     * @param message
     *            message :String ,Number, Map, Object...
     *
     * @deprecated  use {@link #message(String,Object)}
     */
    @Deprecated
    void rPush(String list, Object message);

    /**
     * add message to a queue .
     * @param name queue name.
     * @param message message obj.
     */
    void message(String name, Object message);
}
