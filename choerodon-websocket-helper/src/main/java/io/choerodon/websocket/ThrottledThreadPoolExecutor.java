package io.choerodon.websocket;

import org.springframework.util.ConcurrencyThrottleSupport;

import java.util.concurrent.*;

/**
 * @author crockitwood
 */
public class ThrottledThreadPoolExecutor extends ThreadPoolExecutor{
    private final ConcurrencyThrottleAdapter concurrencyThrottle = new ConcurrencyThrottleAdapter();

    public ThrottledThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.concurrencyThrottle.setConcurrencyLimit(maximumPoolSize);
    }

    public ThrottledThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.concurrencyThrottle.setConcurrencyLimit(maximumPoolSize);
    }

    public ThrottledThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.concurrencyThrottle.setConcurrencyLimit(maximumPoolSize);
    }

    public ThrottledThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.concurrencyThrottle.setConcurrencyLimit(maximumPoolSize);
    }

    @Override
    public void execute(Runnable command) {
        if (isThrottleActive() ) {
            this.concurrencyThrottle.beforeAccess();
            super.execute(new ConcurrencyThrottlingRunnable(command));
        }
        else {
            super.execute(command);
        }
    }

    private static class ConcurrencyThrottleAdapter extends ConcurrencyThrottleSupport {

        @Override
        protected void beforeAccess() {
            super.beforeAccess();
        }

        @Override
        protected void afterAccess() {
            super.afterAccess();
        }
    }
    /**
     * This Runnable calls {@code afterAccess()} after the
     * target Runnable has finished its execution.
     */
    private class ConcurrencyThrottlingRunnable implements Runnable {

        private final Runnable target;

        public ConcurrencyThrottlingRunnable(Runnable target) {
            this.target = target;
        }

        @Override
        public void run() {
            try {
                this.target.run();
            }
            finally {
                concurrencyThrottle.afterAccess();
            }
        }
    }

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyThrottle.setConcurrencyLimit(concurrencyLimit);
    }

    /**
     * Return the maximum number of parallel accesses allowed.
     */
    public final int getConcurrencyLimit() {
        return this.concurrencyThrottle.getConcurrencyLimit();
    }

    /**
     * Return whether this throttle is currently active.
     * @return {@code true} if the concurrency limit for this instance is active
     * @see #getConcurrencyLimit()
     * @see #setConcurrencyLimit
     */
    public final boolean isThrottleActive() {
        return this.concurrencyThrottle.isThrottleActive();
    }

}
