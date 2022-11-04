package io.choerodon.core.config.async;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 猪齿鱼异步线程AutoConfiguration
 * @author gaokuo.dai@zknow.com 2022-11-04
 */
@Configuration
public class ChoerodonAsyncConfigurer implements AsyncConfigurer {

    @Autowired
    private ChoerodonTaskDecorator choerodonTaskDecorator;

    /**
     * @return Async注解默认线程池, 客制化了TaskDecorator
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(128);
        executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("asyncTaskExecutor-");
        // 使用ChoerodonTaskDecorator来同步父子线程的线程变量
        executor.setTaskDecorator(choerodonTaskDecorator);
        executor.initialize();
        return executor;
    }

}
