package io.choerodon.limiter;


import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.choerodon.limiter.lock.DistributedLock;


public class RedisRateLimiter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermitsRedisTemplate.class);


    private static final String LOCK_KEY_SUFFIX = ":lock";

    private PermitsRedisTemplate permitsRedisTemplate;
    private StringRedisTemplate stringRedisTemplate;
    private DistributedLock lock;
    private double permitsPerSecond;
    private double maxBurstSeconds;
    private long expire;


    public RedisRateLimiter(PermitsRedisTemplate permitsRedisTemplate, StringRedisTemplate stringRedisTemplate, DistributedLock lock, double permitsPerSecond,
                            double maxBurstSeconds, long expire) {
        this.permitsRedisTemplate = permitsRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.lock = lock;
        this.permitsPerSecond = permitsPerSecond;
        this.maxBurstSeconds = maxBurstSeconds;
        this.expire = expire;
    }

    /**
     * 获取一个令牌，阻塞一直到获取令牌，返回阻塞等待时间
     *
     * @return time 阻塞等待时间/毫秒
     */
    public long acquire(String key) throws IllegalArgumentException {
        return acquire(key, 1);
    }

    /**
     * 获取指定数量的令牌，如果令牌数不够，则一直阻塞，返回阻塞等待的时间
     *
     * @param permits 需要获取的令牌数
     * @return time 等待的时间/毫秒
     * @throws IllegalArgumentException tokens值不能为负数或零
     */
    public long acquire(String key, int permits) throws IllegalArgumentException {
        long millisToWait = reserve(key, permits);
        LOGGER.info("acquire {} permits for key[{}], waiting for {}ms", permits, key, millisToWait);
        try {
            Thread.sleep(millisToWait);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted when trying to acquire {} permits for key[{}]", permits, key, e);
        }
        return millisToWait;
    }

    /**
     * 在指定时间内获取一个令牌，如果获取不到则一直阻塞，直到超时
     *
     * @param timeout 最大等待时间（超时时间），为0则不等待立即返回
     * @param unit    时间单元
     * @return 获取到令牌则true，否则false
     * @throws IllegalArgumentException
     */
    public boolean tryAcquire(String key, long timeout, TimeUnit unit) throws IllegalArgumentException {
        return tryAcquire(key, 1, timeout, unit);
    }

    /**
     * 在指定时间内获取指定数量的令牌，如果在指定时间内获取不到指定数量的令牌，则直接返回false，
     * 否则阻塞直到能获取到指定数量的令牌
     *
     * @param permits 需要获取的令牌数
     * @param timeout 最大等待时间（超时时间）
     * @param unit    时间单元
     * @return 如果在指定时间内能获取到指定令牌数，则true,否则false
     * @throws IllegalArgumentException tokens为负数或零，抛出异常
     */
    public boolean tryAcquire(String key, int permits, long timeout, TimeUnit unit) throws IllegalArgumentException {
        long timeoutMillis = Math.max(unit.toMillis(timeout), 0);
        checkPermits(permits);

        long millisToWait;
        boolean locked = false;
        try {
            locked = lock.lock(key + LOCK_KEY_SUFFIX, "lock", 60, 2, TimeUnit.SECONDS);
            if (locked) {
                long nowMillis = getNowMillis();
                RedisPermits permit = getPermits(key, nowMillis);
                if (!permit.canAcquire(nowMillis, permits, timeoutMillis)) {
                    return false;
                } else {
                    millisToWait = permit.reserveAndGetWaitLength(nowMillis, permits);
                    permitsRedisTemplate.opsForValue().set(key, permit, expire, TimeUnit.SECONDS);
                }
            } else {
                return false;  //超时获取不到锁，也返回false
            }
        } finally {
            if (locked) {
                lock.unLock(key + LOCK_KEY_SUFFIX, "lock");
            }
        }
        if (millisToWait > 0) {
            try {
                Thread.sleep(millisToWait);
            } catch (InterruptedException e) {

            }
        }
        return true;
    }

    /**
     * 保留指定的令牌数待用
     *
     * @param permits 需保留的令牌数
     * @return time 令牌可用的等待时间
     * @throws IllegalArgumentException tokens不能为负数或零
     */
    private long reserve(String key, int permits) throws IllegalArgumentException {
        checkPermits(permits);
        try {
            lock.lock(key + LOCK_KEY_SUFFIX, "lock", 60, 2, TimeUnit.SECONDS);
            long nowMillis = getNowMillis();
            RedisPermits permit = getPermits(key, nowMillis);
            long waitMillis = permit.reserveAndGetWaitLength(nowMillis, permits);
            permitsRedisTemplate.opsForValue().set(key, permit, expire, TimeUnit.SECONDS);
            return waitMillis;
        } finally {
            lock.unLock(key + LOCK_KEY_SUFFIX, "lock");
        }
    }

    /**
     * 获取令牌桶
     *
     * @return
     */
    private RedisPermits getPermits(String key, long nowMillis) {
        RedisPermits permit = permitsRedisTemplate.opsForValue().get(key);
        if (permit == null) {
            permit = new RedisPermits(permitsPerSecond, maxBurstSeconds, nowMillis);
        }
        return permit;
    }

    /**
     * 获取redis服务器时间
     */
    private long getNowMillis() {
        String luaScript = "return redis.call('time')"; //"TIME";
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>(luaScript, List.class);
        List<String> now = (List<String>)stringRedisTemplate.execute(redisScript, null);
        return now == null ? System.currentTimeMillis() : Long.valueOf(now.get(0))*1000+Long.valueOf(now.get(1))/1000;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "RateLimiter[rate=%3.1fqps]", permitsPerSecond);
    }

    private void checkPermits(long permits) throws IllegalArgumentException {
        Preconditions.checkArgument(permits > 0, "Requested permits (%s) must be positive: ", permits);
    }
}
