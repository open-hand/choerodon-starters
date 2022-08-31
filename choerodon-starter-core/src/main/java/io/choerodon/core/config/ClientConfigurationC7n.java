package io.choerodon.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.DefaultMessageGenerator;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.config.DataSourceBeanPostProcessor;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.controller.MessageController;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.hzero.boot.message.feign.PlatformRemoteService;
import org.hzero.boot.message.feign.fallback.MessageRemoteImpl;
import org.hzero.boot.message.feign.fallback.PlatformRemoteImpl;
import org.hzero.boot.message.registry.MessageInit;
import org.hzero.boot.message.service.MessageAsyncService;
import org.hzero.boot.message.service.MessageGenerator;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.core.client.MessageClientC7n;

/**
 * <p>
 * 消息客户端配置
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 11:51
 */
public class ClientConfigurationC7n {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @ConditionalOnMissingBean
    public MessageClientC7n messageClientC7n(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        return new MessageClientC7n(redisTemplate, objectMapper);
    }

}
