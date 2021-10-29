package io.choerodon.limiter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import io.choerodon.limiter.lock.DistributedLock;


public class RedisRateLimiterFactory {

    private PermitsRedisTemplate permitsRedisTemplate;
    private StringRedisTemplate stringRedisTemplate;
    private DistributedLock distributedLock;

    private Cache<String, RedisRateLimiter> cache = CacheBuilder.newBuilder()
            .initialCapacity(100)  //初始大小
            .maximumSize(10000) // 缓存的最大容量
            .expireAfterAccess(5, TimeUnit.MINUTES) // 缓存在最后一次访问多久之后失效
            .concurrencyLevel(Runtime.getRuntime().availableProcessors()) // 设置并发级别
            .build();

    public RedisRateLimiterFactory(PermitsRedisTemplate permitsRedisTemplate, StringRedisTemplate stringRedisTemplate, DistributedLock distributedLock) {
        this.permitsRedisTemplate = permitsRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.distributedLock = distributedLock;
    }

    /**
     * 创建RateLimiter
     *
     * @param key              Redis key
     * @param permitsPerSecond 每秒放入的令牌数
     * @param maxBurstSeconds  最大存储maxBurstSeconds秒生成的令牌
     * @param expire           该令牌桶的redis tty/秒
     * @return RateLimiter
     */
    public RedisRateLimiter build(String key, double permitsPerSecond, double maxBurstSeconds, int expire) {
        if (cache.getIfPresent(key) == null) {
            synchronized (this) {
                if (cache.getIfPresent(key) == null) {
                    cache.put(key, new RedisRateLimiter(permitsRedisTemplate, stringRedisTemplate, distributedLock, permitsPerSecond,
                            maxBurstSeconds, expire));
                }
            }
        }
        return cache.getIfPresent(key);
    }
}
