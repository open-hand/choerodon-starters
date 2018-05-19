package io.choerodon.event.consumer.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.EventConsumer;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 消息消费端创建工厂接口
 *
 * @author flyleft
 */
public interface MessageConsumerFactory {

    void createConsumer(Method method, Object object, EventListener eventListener, TypeReference payLoadType);

    default void createConsumers(List<EventConsumer> eventConsumers) {

    }

}
