package io.choerodon.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.core.client.MessageClientC7n;
import io.choerodon.core.config.async.ChoerodonAsyncConfigurer;
import io.choerodon.core.config.async.ChoerodonTaskDecorator;
import io.choerodon.core.config.async.plugin.ChoerodonTaskDecoratorPlugin;
import io.choerodon.core.config.async.plugin.HZeroEncryptContextTaskDecoratorPlugin;
import io.choerodon.core.config.async.plugin.HZeroRequestContextTaskDecoratorPlugin;
import io.choerodon.core.config.async.plugin.HZeroSecurityContextTaskDecoratorPlugin;
import io.choerodon.core.transaction.event.AfterTransactionCommitTaskExecutor;
import io.choerodon.core.transaction.event.AfterTransactionCommitTaskExecutorImpl;

/**
 * <p>
 * 消息客户端配置
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 11:51
 */
@Configuration
@Import(ChoerodonAsyncConfigurer.class)
public class ChoerodonStarterCoreAutoConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public MessageClientC7n messageClientC7n(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        return new MessageClientC7n(redisTemplate, objectMapper);
    }

    /**
     * @param choerodonTaskDecoratorPlugins 自定义异步线程装饰插件集合
     * @return 猪齿鱼异步线程装饰器, 包含了三个标准插件和自动注入的自定义的插件
     */
    @Bean
    public ChoerodonTaskDecorator choerodonTaskDecorator(@Autowired List<ChoerodonTaskDecoratorPlugin<?>> choerodonTaskDecoratorPlugins) {
        if(choerodonTaskDecoratorPlugins == null) {
            choerodonTaskDecoratorPlugins = new ArrayList<>();
        }
        choerodonTaskDecoratorPlugins.add(new HZeroRequestContextTaskDecoratorPlugin());
        choerodonTaskDecoratorPlugins.add(new HZeroSecurityContextTaskDecoratorPlugin());
        choerodonTaskDecoratorPlugins.add(new HZeroEncryptContextTaskDecoratorPlugin());

        return new ChoerodonTaskDecorator(choerodonTaskDecoratorPlugins);
    }

    /**
     * @param choerodonTaskDecorator 猪齿鱼异步线程装饰器
     * @return 数据库事务提交后任务注册机专用线程池
     */
    @Bean
    public ThreadPoolTaskExecutor contextCopyDecoratorExecutor(@Autowired ChoerodonTaskDecorator choerodonTaskDecorator) {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(16);
        pool.setMaxPoolSize(128);
        pool.setQueueCapacity(256);
        pool.setThreadNamePrefix("HZeroContextCopyingDecoratorExecutor");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        if(choerodonTaskDecorator != null) {
            pool.setTaskDecorator(choerodonTaskDecorator);
        }
        return pool;
    }

    /**
     * @param threadPoolTaskExecutor 线程池
     * @return 数据库事务提交后任务注册机
     */
    @Bean
    @ConditionalOnMissingBean
    public AfterTransactionCommitTaskExecutor afterTransactionCommitTaskExecutor(@Autowired @Qualifier("contextCopyDecoratorExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new AfterTransactionCommitTaskExecutorImpl(threadPoolTaskExecutor);
    }

}
