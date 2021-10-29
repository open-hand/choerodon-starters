package io.choerodon.limiter.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class DistributedLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);


    private StringRedisTemplate stringRedisTemplate;

    private final long waitMillisPer = 50L; // 重试间隔/毫秒


    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private static final Long RELEASE_LOCK_SUCCESS_RESULT = 1L;

    public DistributedLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 尝试获取锁（立即返回）
     * @param key  锁的redis key
     * @param value 锁的value
     * @param expire 过期时间/秒
     * @return 是否获取成功
     */
    public boolean lock(String key, String value, long expire) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁，并至多等待timeout时长
     *
     * @param key  锁的redis key
     * @param value 锁的value
     * @param expire 过期时间/秒
     * @param timeout 超时时长
     * @param unit    时间单位
     * @return 是否获取成功
     */
    public boolean lock(String key, String value, long expire, long timeout, TimeUnit unit) {
        long waitMillis = unit.toMillis(timeout);
        long waitAlready = 0;

        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.SECONDS) && waitAlready < waitMillis) {
            try {
                Thread.sleep(waitMillisPer);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted when trying to get a lock. key: {}", key, e);
            }
            waitAlready += waitMillisPer;
        }

        if (waitAlready < waitMillis) {
            return true;
        }
        LOGGER.warn("<====== lock {} failed after waiting for {} ms", key, waitAlready);
        return false;
    }

    /**
     * 释放锁
     * @param key  锁的redis key
     * @param value 锁的value
     */
    public boolean unLock(String key, String value) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT, Long.class);
        long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), value);
        return Objects.equals(result, RELEASE_LOCK_SUCCESS_RESULT);
    }
}
