package io.choerodon.message.impl.rabbit;

import io.choerodon.message.annotation.QueueMonitor;
import io.choerodon.message.annotation.TopicMonitor;
import io.choerodon.message.impl.MethodReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author njq.niu@hand-china.com
 */
public class ListenerContainerFactory implements ApplicationContextAware, SmartLifecycle {

    private Logger logger = LoggerFactory.getLogger(ListenerContainerFactory.class);

    private volatile boolean running = false;

    private static final int PHASE = 9999;

    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("defaultRabbitAdmin")
    private RabbitAdmin rabbitAdmin;

    @Autowired
    @Qualifier("defaultDirectExchange")
    private DirectExchange directExchange;

    @Autowired
    @Qualifier("defaultTopicExchange")
    private TopicExchange topicExchange;

    @Autowired
    @Qualifier("defaultConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    private List<SimpleMessageListenerContainer> listenerContainerList = new ArrayList<>();


    @Override
    public void start() {
        if (!isRunning()) {
            Map<String, Object> queueMonitors = applicationContext.getBeansWithAnnotation(QueueMonitor.class);
            queueMonitors.forEach((k, v) -> {
                Class clazz = AopUtils.getTargetClass(v);
                QueueMonitor qm = (QueueMonitor) clazz.getAnnotation(QueueMonitor.class);
                final String queueName = qm.queue();
                Queue queue = new Queue(k + "-" + queueName, true);
                rabbitAdmin.declareQueue(queue);
                rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(queueName));

                SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
                listenerContainerList.add(container);
                String mn = MethodReflectUtils.getQueueMethodName(qm.method(), v);
                CustomMessageListenerAdapter adapter = new CustomMessageListenerAdapter(v, mn, jackson2JsonMessageConverter);
                container.setRabbitAdmin(rabbitAdmin);
                container.setMessageListener(adapter);
                container.setQueueNames(queue.getName());
                container.start();
                if (logger.isDebugEnabled()) {
                    logger.debug("Start message listener {} on {}", k + "." + mn, queue.getName());
                }
            });

            Map<String, Object> topicMonitors = applicationContext.getBeansWithAnnotation((TopicMonitor.class));
            topicMonitors.forEach((k, v) -> {
                Class clazz = AopUtils.getTargetClass(v);
                TopicMonitor qm = (TopicMonitor) clazz.getAnnotation(TopicMonitor.class);
                final String[] channelNames = qm.channel();
                Queue queue = rabbitAdmin.declareQueue();
                SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
                listenerContainerList.add(container);
                for (String channelName : channelNames) {
                    rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(channelName));
                }

                String tmn = MethodReflectUtils.getTopicMethodName(qm.method(), v);
                CustomMessageListenerAdapter adapter = new CustomMessageListenerAdapter(v, tmn, jackson2JsonMessageConverter);
                container.setRabbitAdmin(rabbitAdmin);
                container.setMessageListener(adapter);
                container.setQueueNames(queue.getName());
                container.start();
                if (logger.isDebugEnabled()) {
                    logger.debug("Start topic listener {} on {}", k + "." + tmn, queue.getName());
                }
            });
            running = true;
        }
    }

    @Override
    public void stop() {
        stop(null);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        if (isRunning()) {
            listenerContainerList.forEach(c -> {
                c.stop();
            });
            running = false;
        }
        if (callback != null) {callback.run();}
    }

    @Override
    public int getPhase() {
        return PHASE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static class CustomMessageListenerAdapter extends MessageListenerAdapter {

        public CustomMessageListenerAdapter(Object delegate, String defaultListenerMethod, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
            super(delegate, defaultListenerMethod);
            this.setMessageConverter(jackson2JsonMessageConverter);
        }

        @Override
        protected Object invokeListenerMethod(String methodName, Object[] arguments, Message originalMessage) {
            arguments = new Object[]{arguments[0], originalMessage.getMessageProperties().getReceivedRoutingKey()};
            return super.invokeListenerMethod(methodName, arguments, originalMessage);
        }

    }
}
