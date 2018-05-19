package io.choerodon.event.consumer.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.event.consumer.DuplicateRemoveListener;
import io.choerodon.event.consumer.EventConsumerAutoConfiguration;
import io.choerodon.event.consumer.EventConsumerProperties;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.retry.RetryFactory;
import io.choerodon.event.consumer.rocketmq.MessageListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * rocketmq的消费端创建工厂
 *
 * @author flyleft
 */
public class RocketMessageConsumerFactory implements MessageConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMessageConsumerFactory.class);

    private EventConsumerProperties consumerProperties;

    @Autowired
    public RocketMessageConsumerFactory(DataSourceTransactionManager transactionManager,
                                        EventConsumerProperties consumerProperties,
                                        DuplicateRemoveListener listener,
                                        Optional<RetryFactory> retryFactory) {
        this.consumerProperties = consumerProperties;
    }

    @Override
    public void createConsumer(Method method, Object object, EventListener eventListener, TypeReference payLoadType) {
        String key = EventConsumerAutoConfiguration.EVENT_BEAN_PREFIX + StringUtils.join(eventListener.topic());
        try {
            ApplicationContextHelper.getContext().getBean(key);
        } catch (BeansException e) {
            createRocketConsumer(key, new MsgExecuteBean(eventListener, method, object, null));
        }
    }

    /**
     * 创建rocketmq的消费端
     *
     * @param key  该消费端在spring的位移标识
     * @param bean 消息执行信息封装的bean
     */
    private void createRocketConsumer(String key, final MsgExecuteBean bean) {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(key);
        consumer.setNamesrvAddr(consumerProperties.getRocketmq().getNamesrvAddr());
        consumer.setConsumeThreadMin(consumerProperties.getRocketmq().getConsumeThreadMin());
        consumer.setConsumeThreadMax(consumerProperties.getRocketmq().getConsumeThreadMax());
        MessageListener messageListener = new MessageListener();
        messageListener.setMessageProcessor((MessageExt messageExt) -> true);
        consumer.registerMessageListener(messageListener);
        try {
            consumer.subscribe(bean.eventListener.topic(), "*");
            ApplicationContextHelper.getSpringFactory().registerSingleton(key, consumer);
            consumer.start();
        } catch (MQClientException e) {
            LOGGER.warn("error.RocketMessageConsumerFactory.createRocketConsumer {}", e.toString());
        }
    }


}
