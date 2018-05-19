package io.choerodon.event.consumer.factory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.event.consumer.DuplicateRemoveListener;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.retry.RetryFactory;
import io.choerodon.event.consumer.EventConsumerAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * redis消费端创建工厂
 * @author flyleft
 */
public class RedisMessageConsumerFactory implements MessageConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageConsumerFactory.class);

    private RedisConnectionFactory connectionFactory;

    @Autowired
    public RedisMessageConsumerFactory(DataSourceTransactionManager transactionManager,
                                       RedisConnectionFactory connectionFactory,
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
            createRedisContainer(key, new MsgExecuteBean(eventListener, method, object, null));
        }
    }

    private void createRedisContainer(final String key, final MsgExecuteBean bean) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(RedisContainer.class);
        MessageListener messageListener = (Message message, byte[] bytes) -> LOGGER.info("message {}", message);
        builder.addConstructorArgValue(messageListener).addConstructorArgValue(new ChannelTopic(bean.eventListener.topic()));

        ApplicationContextHelper.getSpringFactory().registerBeanDefinition(key,
                builder.getRawBeanDefinition());
    }

    static class RedisContainer extends RedisMessageListenerContainer {
        public RedisContainer(RedisConnectionFactory connectionFactory,
                              MessageListener listener,
                              List<ChannelTopic> topics) {
            super();
            super.setConnectionFactory(connectionFactory);
            super.addMessageListener(listener, topics);
        }

    }
}
