package io.choerodon.event.producer.execute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import io.choerodon.core.event.EventPayload;
import io.choerodon.core.exception.CommonException;
import io.choerodon.event.producer.check.EventProducerRecord;
import io.choerodon.event.producer.check.mapper.EventProducerRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author flyleft
 * 2018/5/12
 */
public class EventProducerTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducerTemplate.class);

    @Value("${spring.application.name}")
    private String serviceName;

    private DataSourceTransactionManager transactionManager;

    private EventStoreClient eventStoreClient;

    private EventProducerRecordMapper eventProducerRecordMapper;

    private final ObjectMapper mapper = new ObjectMapper();

    public EventProducerTemplate(DataSourceTransactionManager transactionManager,
                                 EventStoreClient eventStoreClient, EventProducerRecordMapper eventProducerRecordMapper) {
        this.transactionManager = transactionManager;
        this.eventStoreClient = eventStoreClient;
        this.eventProducerRecordMapper = eventProducerRecordMapper;
    }

    /**
     * 执行事件发送，发送到消息队列的消息为单条
     *
     * @param producerType 回查业务类型
     * @param consumerType 消费业务类型
     * @param topic        要发送到的消息队列的队列名
     * @param payload      要发送到的消息队列的消息体
     * @param executer     业务代码执行器，可使用lambda简化使用
     * @return 是否执行成功
     */
    public Exception execute(final String producerType,
                             final String consumerType,
                             final String topic,
                             final Object payload,
                             final EventExecuter executer) {
        final List<EventMessage> messageList = Collections.singletonList(
                new EventMessage(topic, new EventPayload<>(producerType, payload)));
        return execute(producerType, consumerType, messageList, executer, generateUuid());
    }

    /**
     * 执行事件发送，发送到消息队列的消息为单条
     *
     * @param type     业务类型
     * @param topic    要发送到的消息队列的队列名
     * @param payload  要发送到的消息队列的消息体
     * @param executer 业务代码执行器，可使用lambda简化使用
     * @return 是否执行成功
     */
    public Exception execute(final String type,
                             final String topic,
                             final Object payload,
                             final EventExecuter executer) {
        final List<EventMessage> messageList = Collections.singletonList(
                new EventMessage(topic, new EventPayload<>(type, payload)));
        return execute(type, type, messageList, executer, generateUuid());
    }

    /**
     * 执行事件发送，发送到消息队列的消息为单条
     *
     * @param type     业务类型
     * @param topic    要发送到的消息队列的队列名
     * @param payload  要发送到的消息队列的消息体
     * @param uuid     手动传入uuid
     * @param executer 业务代码执行器，可使用lambda简化使用
     * @return 是否执行成功
     */
    public Exception execute(final String type,
                             final String topic,
                             final Object payload,
                             final EventExecuter executer,
                             final String uuid) {
        final List<EventMessage> messageList = Collections.singletonList(
                new EventMessage(topic, new EventPayload<>(type, payload)));
        return execute(type, type, messageList, executer, uuid);
    }


    /**
     * 执行事件发送，发送到消息队列的消息为多条
     *
     * @param producerType 回查业务类型
     * @param consumerType 消费业务类型
     * @param messageList  要发送到消息队列的消息列表
     * @param executer     业务代码执行器，可使用lambda简化使用
     * @param uuid         手动传入uuid
     * @return 是否执行成功
     */
    public Exception execute(final String producerType,
                             final String consumerType,
                             final List<EventMessage> messageList,
                             final EventExecuter executer,
                             final String uuid) {
        messageList.forEach(t -> {
            t.getPayload().setUuid(uuid);
            t.getPayload().setBusinessType(consumerType);
        });
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        //发送事件创建消息
        try {
            final EventRecord eventRecord = new EventRecord(uuid, producerType, serviceName);
            eventStoreClient.createEvent(eventRecord);
        } catch (HystrixRuntimeException e) {
            LOGGER.info("event-store-service timeout {}", e);
            return new CommonException("error.eventStoreService.timeout");
        } catch (Exception e) {
            LOGGER.info("create event failed {}", e);
            return e;
        }
        try {
            //执行具体业务
            executer.doSomething(uuid);
            String messages = convert(messageList);
            if (eventProducerRecordMapper.insert(
                    new EventProducerRecord(uuid, producerType, new Timestamp(System.currentTimeMillis()))) != 1) {
                throw new CommonException("error.eventProducerRecord.insert");
            }
            eventStoreClient.preConfirmEvent(uuid, messages);
            transactionManager.commit(status);
        } catch (HystrixRuntimeException e) {
            LOGGER.info("event-store-service timeout {} ", e);
            transactionManager.rollback(status);
            return new CommonException("error.eventStoreService.timeout");
        } catch (Exception e) {
            LOGGER.info("execute failed {}", e);
            //异常回滚
            transactionManager.rollback(status);
            try {
                eventStoreClient.cancelEvent(uuid);
            } catch (Exception e1) {
                LOGGER.info("cancel event failed {}", e1);
            }
            return e;
        }
        try {
            eventStoreClient.confirmEvent(uuid);
        } catch (HystrixRuntimeException e) {
            LOGGER.info("event-store-service timeout {}", e);
            return new CommonException("error.eventStoreService.timeout");
        } catch (Exception e1) {
            LOGGER.info("confirm event failed {}", e1);
        }
        return null;
    }

    /**
     * 执行事件发送，发送到消息队列的消息为多条
     *
     * @param producerType 回查业务类型
     * @param consumerType 消费业务类型
     * @param messageList  要发送到消息队列的消息列表
     * @param executer     业务代码执行器，可使用lambda简化使用
     * @return 是否执行成功
     */
    public Exception execute(final String producerType,
                             final String consumerType,
                             final List<EventMessage> messageList,
                             final EventExecuter executer) {
        final String uuid = generateUuid();
        return execute(producerType, consumerType, messageList, executer, uuid);
    }

    private String convert(List<EventMessage> messages) {
        List<EventSendMsg> sendMsgs = messages.stream().map(t -> {
            String msg = null;
            try {
                msg = mapper.writeValueAsString(t.getPayload());
            } catch (JsonProcessingException e) {
                LOGGER.warn("JsonProcessingException {}", e.getMessage());
            }
            return new EventSendMsg(t.getTopic(), msg);
        }).filter(t -> t.getPayload() != null).collect(Collectors.toList());
        if (sendMsgs.size() != messages.size()) {
            throw new CommonException("error.eventProducerTemplate.convert");
        }
        try {
            return mapper.writeValueAsString(sendMsgs);
        } catch (Exception e) {
            LOGGER.warn("JsonProcessingException {}", e.getMessage());
            return null;
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
