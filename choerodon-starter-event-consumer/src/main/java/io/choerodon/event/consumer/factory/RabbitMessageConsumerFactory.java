package io.choerodon.event.consumer.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.event.consumer.DuplicateRemoveListener;
import io.choerodon.event.consumer.EventConsumerAutoConfiguration;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.retry.RetryFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * rabbitmq创建消费端的工厂
 *
 * @author flyleft
 */
public class RabbitMessageConsumerFactory implements MessageConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerFactory.class);

    private ConnectionFactory connectionFactory;

    @Autowired
    public RabbitMessageConsumerFactory(DataSourceTransactionManager transactionManager,
                                        ConnectionFactory connectionFactory,
                                        DuplicateRemoveListener listener,
                                        Optional<RetryFactory> retryFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void createConsumer(Method method, Object object, EventListener eventListener, TypeReference payLoadType) {
        String key = EventConsumerAutoConfiguration.EVENT_BEAN_PREFIX + StringUtils.join(eventListener.topic());
        try {
            ApplicationContextHelper.getContext().getBean(key);
        } catch (BeansException e) {
            createRabbitContainer(key, new MsgExecuteBean(eventListener, method, object, null));
        }
    }

    private void createRabbitContainer(final String key, final MsgExecuteBean msgExecuteBean) {
        Queue queue = new Queue(msgExecuteBean.eventListener.topic());
        ApplicationContextHelper.getSpringFactory().registerSingleton(key + "queue", queue);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setQueues(queue);
        ChannelAwareMessageListener listener = (Message message, Channel channel) -> LOGGER.info("message {}", message);
        container.setMessageListener(listener);
        ApplicationContextHelper.getSpringFactory().registerSingleton(key, container);
    }


}
