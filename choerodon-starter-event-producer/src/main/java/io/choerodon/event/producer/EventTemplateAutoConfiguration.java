package io.choerodon.event.producer;

import io.choerodon.core.ChoerodonCoreAutoConfiguration;
import io.choerodon.event.producer.check.DefaultEventBackCheckController;
import io.choerodon.event.producer.check.mapper.EventProducerRecordMapper;
import io.choerodon.event.producer.execute.EventProducerTemplate;
import io.choerodon.event.producer.execute.EventStoreClient;
import io.choerodon.event.producer.execute.EventStoreClientFallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * 工具包的AutoConfiguration类，用于加载一些对象到spring工厂中
 *
 * @author flyleft
 */
@ComponentScan
@Configuration
@Import(ChoerodonCoreAutoConfiguration.class)
public class EventTemplateAutoConfiguration {

    @Bean
    public EventStoreClientFallback eventStoreClientFallback() {
        return new EventStoreClientFallback();
    }

    /**
     * spring工厂加入EventTemplate实体
     *
     * @param transactionManager 事务处理器
     * @param eventStoreClient   event store服务调用feign
     * @param eventProducerRecordMapper eventProducerRecord的mapper
     * @return 创建的EventTemplate数据一致性调用模板
     */
    @Bean
    @Autowired
    public EventProducerTemplate eventProducerTemplate(DataSourceTransactionManager transactionManager,
                                             EventStoreClient eventStoreClient,
                                             EventProducerRecordMapper eventProducerRecordMapper) {

        return new EventProducerTemplate(transactionManager, eventStoreClient, eventProducerRecordMapper);
    }

    @Bean
    @Autowired
    public DefaultEventBackCheckController defaultEventBackCheckController(
            EventProducerRecordMapper eventProducerRecordMapper) {
        return new DefaultEventBackCheckController(eventProducerRecordMapper);
    }

}
