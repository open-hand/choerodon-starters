package io.choerodon.core.config;

import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.core.client.MessageClientC7n;
import io.choerodon.core.transaction.event.AfterTransactionCommitTaskExecutor;
import io.choerodon.core.transaction.event.AfterTransactionCommitTaskExecutorImpl;
import io.choerodon.core.utils.HZeroContextCopyDecorator;

/**
 * <p>
 * 消息客户端配置
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 11:51
 */
public class ChoerodonStarterCoreAutoConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public MessageClientC7n messageClientC7n(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        return new MessageClientC7n(redisTemplate, objectMapper);
    }

    @Bean
    public ThreadPoolTaskExecutor contextCopyDecoratorExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(16);
        pool.setMaxPoolSize(128);
        pool.setQueueCapacity(32);
        pool.setThreadNamePrefix("HZeroContextCopyingDecoratorExecutor");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        pool.setTaskDecorator(HZeroContextCopyDecorator.getInstance());
        return pool;
    }

    @Bean
    @ConditionalOnMissingBean
    public AfterTransactionCommitTaskExecutor afterTransactionCommitTaskExecutor(@Autowired @Qualifier("contextCopyDecoratorExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new AfterTransactionCommitTaskExecutorImpl(threadPoolTaskExecutor);
    }

}
