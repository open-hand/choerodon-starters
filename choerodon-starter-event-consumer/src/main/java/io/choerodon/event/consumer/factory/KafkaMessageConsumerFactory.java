package io.choerodon.event.consumer.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.CommonUtils;
import io.choerodon.event.consumer.EventConsumerProperties;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.event.consumer.domain.EventConsumer;
import io.choerodon.event.consumer.domain.MsgExecuteBean;
import io.choerodon.event.consumer.handler.MsgHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * kafka的消费端创建工厂
 *
 * @author flyleft
 */
public class KafkaMessageConsumerFactory implements MessageConsumerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumerFactory.class);

    private ObjectMapper mapper = new ObjectMapper();

    private Properties kafkaProperties;

    private ExecutorService executorService;

    private MsgHandler msgHandler;

    private EventConsumerProperties consumerProperties;


    /**
     * 构造器
     *
     * @param kafkaProperties    kafka配置类
     * @param msgHandler         消息处理器
     * @param executorService  executorService
     * @param consumerProperties 全局配置bean
     */
    public KafkaMessageConsumerFactory(Properties kafkaProperties,
                                       MsgHandler msgHandler,
                                       EventConsumerProperties consumerProperties,
                                       ExecutorService executorService) {
        this.kafkaProperties = kafkaProperties;
        this.msgHandler = msgHandler;
        this.consumerProperties = consumerProperties;
        this.executorService = executorService;
    }

    @Override
    public void createConsumer(final Method method, final Object object, final EventListener listener, final TypeReference payLoadType) {
        //create single consumer
    }

    @Override
    public void createConsumers(List<EventConsumer> eventConsumers) {
        List<String> topics = new ArrayList<>();
        for (EventConsumer eventConsumer : eventConsumers) {
            topics.add(eventConsumer.eventListener.topic());
        }
        executorService.execute(() -> {
            try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(kafkaProperties)) {
                kafkaConsumer.subscribe(topics);
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (TopicPartition partition : records.partitions()) {
                        List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                        receiveMsg(partitionRecords, kafkaConsumer, eventConsumers, partition);
                    }
                }
            }
        });
    }

    private void receiveMsg(final List<ConsumerRecord<String, String>> partitionRecords,
                         final KafkaConsumer<String, String> kafkaConsumer,
                         final List<EventConsumer> eventConsumers ,
                         final TopicPartition partition) {
        try {
            partitionRecords.parallelStream().forEach(t -> {
                String businessType = CommonUtils.getBusinessType(t.value());
                if (StringUtils.isEmpty(businessType)) {
                    LOGGER.warn("businessType is null, skip this payload {}", t.value());
                }
                EventConsumer eventConsumer = getEventConsumer(eventConsumers, t.topic(), businessType);
                if (eventConsumer != null) {
                    final MsgExecuteBean bean = new MsgExecuteBean(eventConsumer.eventListener,
                            eventConsumer.method, eventConsumer.object, consumerProperties);
                    try {
                        bean.setPayloadJson(t.value());
                        EventPayload<?> payload = mapper.readValue(t.value(), eventConsumer.payLoadType);
                        bean.setPayload(payload);
                        bean.setMessageTimestamp(t.timestamp());
                        bean.setKafkaPartition(t.partition());
                        bean.setKafkaOffset(t.offset());
                        msgHandler.execute(bean);
                    } catch (IOException e) {
                        LOGGER.warn("message deserialize error, {}", e.toString());
                    }
                }
            });
            long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
            kafkaConsumer.commitSync(Collections.singletonMap(partition,
                    new OffsetAndMetadata(lastOffset + 1)));
        } catch (Exception e) {
            long reConsumeOffset = partitionRecords.get(0).offset();
            LOGGER.warn("consume message failed, seek partition {} offset to {} error {}",
                    partition, reConsumeOffset, e.toString());
            kafkaConsumer.seek(partition, reConsumeOffset);
        }
    }

    private EventConsumer getEventConsumer(List<EventConsumer> eventConsumers, String topic, String businessType) {
        for (EventConsumer eventConsumer : eventConsumers) {
            List<String> businessTypes = Arrays.asList(eventConsumer.eventListener.businessType());
            if (eventConsumer.eventListener.topic().equals(topic) && businessTypes.contains(businessType)) {
                return eventConsumer;
            }
        }
        return null;
    }

}

