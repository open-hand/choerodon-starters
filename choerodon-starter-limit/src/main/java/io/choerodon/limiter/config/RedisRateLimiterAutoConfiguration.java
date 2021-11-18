package io.choerodon.limiter.config;


import org.hzero.core.message.MessageAccessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;

import io.choerodon.limiter.PermitsRedisTemplate;
import io.choerodon.limiter.RedisGitlabPermitsProperties;
import io.choerodon.limiter.RedisRateLimiter;
import io.choerodon.limiter.RedisRateLimiterFactory;
import io.choerodon.limiter.lock.DistributedLock;


@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties({RedisGitlabPermitsProperties.class})
public class RedisRateLimiterAutoConfiguration {

    @Bean
    public DistributedLock distributedLock(StringRedisTemplate stringRedisTemplate) {
        return new DistributedLock(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public PermitsRedisTemplate permitsRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        PermitsRedisTemplate template = new PermitsRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public RedisRateLimiterFactory redisRateLimiterFactory(PermitsRedisTemplate permitsRedisTemplate, StringRedisTemplate stringRedisTemplate, DistributedLock distributedLock) {
        return new RedisRateLimiterFactory(permitsRedisTemplate, stringRedisTemplate, distributedLock);
    }

    @Bean
    public RedisRateLimiter defaultGitlabRedisLimitAspect(RedisRateLimiterFactory redisRateLimiterFactory, RedisGitlabPermitsProperties redisGitlabPermitsProperties) {
        return redisRateLimiterFactory.build("gitlab",
                redisGitlabPermitsProperties.getPermitsPerSecond(),
                redisGitlabPermitsProperties.getMaxBurstSeconds(),
                redisGitlabPermitsProperties.getExpire());
    }

    @PostConstruct
    public void addLocal() {
        MessageAccessor.addBasenames("classpath:messages/message_choerodon_starter_limit");
    }

}
