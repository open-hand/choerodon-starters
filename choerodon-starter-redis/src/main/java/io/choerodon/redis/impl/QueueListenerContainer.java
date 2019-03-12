/*
 * #{copyright}#
 */

package io.choerodon.redis.impl;

import io.choerodon.redis.IQueueMessageListener;
import io.choerodon.redis.annotation.QueueMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.SchedulingAwareRunnable;
import org.springframework.util.Assert;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class QueueListenerContainer implements InitializingBean, DisposableBean, SmartLifecycle {

    @Autowired
    private ApplicationContext applicationContext;

    private Logger logger = LoggerFactory.getLogger(QueueListenerContainer.class);

    private RedisConnectionFactory connectionFactory;

    private static final int PHASE = 9999;

    private static final long MIN_RECOVERY_INTERVAL = 2000L;

    private static final long DEFAULT_RECOVERY_INTERVAL = 5000L;

    /**
     * 100ms.
     */
    private static final long IDLE_SLEEP_TIME = 100L;

    private long recoveryInterval = DEFAULT_RECOVERY_INTERVAL;

    private volatile boolean running = false;

    private ExecutorService executorService;

    private List<IQueueMessageListener<?>> listeners;

    private List<MonitorTask> monitorTaskList = new ArrayList<>();

    private RedisSerializer<String> stringRedisSerializer;

    public RedisConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public long getRecoveryInterval() {
        return recoveryInterval;
    }

    public void setRecoveryInterval(long recoveryInterval) {
        this.recoveryInterval = recoveryInterval;
        if (recoveryInterval < MIN_RECOVERY_INTERVAL) {
            if (logger.isWarnEnabled()) {
                logger.warn("minimum for recoveryInterval is {}", MIN_RECOVERY_INTERVAL);
            }
            this.recoveryInterval = MIN_RECOVERY_INTERVAL;
        }
    }

    public List<IQueueMessageListener<?>> getListeners() {
        return listeners;
    }

    public void setListeners(List<IQueueMessageListener<?>> listeners) {
        this.listeners = listeners;
    }

    public RedisSerializer<String> getStringRedisSerializer() {
        return stringRedisSerializer;
    }

    @Autowired
    public void setStringRedisSerializer(RedisSerializer<String> stringRedisSerializer) {
        this.stringRedisSerializer = stringRedisSerializer;
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        if (!running) {
            running = true;

            if (logger.isDebugEnabled()) {
                logger.debug("startup success");
            }
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            running = false;
            monitorTaskList.forEach(MonitorTask::stop);
            executorService.shutdownNow();
            if (logger.isDebugEnabled()) {
                logger.debug("shutdown complete");
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return PHASE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        Map<String, Object> lts = applicationContext.getBeansWithAnnotation(QueueMonitor.class);
        lts.forEach((k, v) -> {
            Class clazz = AopUtils.getTargetClass(v);
            QueueMonitor qm = (QueueMonitor) clazz.getAnnotation(QueueMonitor.class);
            final String queue = qm.queue();
            String mn = MethodReflectUtils.getQueueMethodName(qm.method(), v);
            List<Method> methods = MethodReflectUtils.findMethod(clazz, new MethodReflectUtils.FindDesc(mn, 2));
            if (methods.isEmpty()) {
                if (logger.isErrorEnabled()) {
                    logger.error("can not find proper method of name '{}' for bean {}", mn, v);
                }
                return;
            }
            final Method method = methods.get(0);
            IQueueMessageListener qml = new SimpleQueueListener(queue, v, method);
            listeners.add(qml);

        });
        executorService = Executors.newFixedThreadPool(listeners.size());
        for (IQueueMessageListener<?> receiver : listeners) {
            MonitorTask task = new MonitorTask(receiver);
            monitorTaskList.add(task);
            executorService.execute(task);
        }
    }

    private static class SimpleQueueListener implements IQueueMessageListener {
        private String queue;
        private Object target;
        private Method method;
        private RedisSerializer redisSerializer;
        private Logger logger;

        SimpleQueueListener(String queue, Object target, Method method) {
            this.queue = queue;
            this.target = target;
            this.method = method;
            this.redisSerializer = MethodReflectUtils.getProperRedisSerializer(method.getParameterTypes()[0]);
            this.logger = LoggerFactory.getLogger(target.getClass());
        }

        @Override
        public String getQueue() {
            return queue;
        }

        @Override
        public RedisSerializer getRedisSerializer() {
            return redisSerializer;
        }

        @Override
        public void onQueueMessage(Object message, String queue) {
            try {
                method.invoke(target, message, queue);
            } catch (Exception e) {
                Throwable thr = e;
                while (thr.getCause() != null) {
                    thr = thr.getCause();
                }
                if (logger.isErrorEnabled()) {
                    logger.error(thr.getMessage(), thr);
                }
            }
        }
    }

    /**
     * @param <T>
     */
    private class MonitorTask<T> implements SchedulingAwareRunnable {

        private IQueueMessageListener<T> receiver;
        private RedisConnection connection;

        private boolean running = false;

        MonitorTask(IQueueMessageListener<T> receiver) {
            this.receiver = receiver;
            Assert.notNull(receiver, "receiver is null.");
            Assert.hasText(receiver.getQueue(), "queue is not valid");
        }

        public void stop() {
            running = false;
            safeClose(true);
        }

        @Override
        public void run() {
            running = true;
            T message;
            while (running) {
                try {
                    if (connection == null) {
                        connection = connectionFactory.getConnection();
                    }
                    message = fetchMessage(connection, receiver.getQueue());
                    if (message == null) {
                        sleep_(IDLE_SLEEP_TIME);
                        continue;
                    }
                } catch (Throwable thr) {
                    if (!running) {
                        break;
                    }
                    safeClose();
                    if (logger.isDebugEnabled()) {
                        logger.error("exception occurred while get message from queue [" + receiver.getQueue() + "]",
                                thr);
                        logger.debug("try recovery after {}ms", getRecoveryInterval());
                    }
                    sleep_(getRecoveryInterval());
                    continue;
                }
                try {
                    receiver.onQueueMessage(message, receiver.getQueue());
                } catch (Throwable thr) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("exception occurred while receiver consume message.", thr);
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("stop monitor:" + this);
            }
            safeClose();
        }

        T fetchMessage(RedisConnection connection, String queue) {
            List<byte[]> bytes = connection.bLPop(0, stringRedisSerializer.serialize(queue));
            if (bytes == null || bytes.isEmpty()) {
                return null;
            }
            return receiver.getRedisSerializer().deserialize(bytes.get(1));
        }

        void safeClose(boolean... closeNative) {
            if (connection != null) {
                try {
                    if (closeNative.length > 0 && closeNative[0]) {
                        // close native connection to interrupt blocked
                        // operation
                        ((Jedis) connection.getNativeConnection()).disconnect();
                    }
                    connection.close();
                } catch (Exception e) {
                    // if (logger.isErrorEnabled()) {
                    // logger.error(e.getMessage(), e);
                    // }
                }
            }
            connection = null;
        }

        void sleep_(long time) {
            try {
                Thread.sleep(time);
            } catch (Exception e) {
                // if (logger.isErrorEnabled()) {
                // logger.error(e.getMessage(), e);
                // }
            }
        }

        @Override
        public boolean isLongLived() {
            return true;
        }
    }
}
