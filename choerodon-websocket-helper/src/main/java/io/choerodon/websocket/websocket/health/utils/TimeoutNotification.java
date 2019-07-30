package io.choerodon.websocket.websocket.health.utils;

/**
 * time rotation timeout callback processing.
 * <p>
 * notifier implementations must not block and exceptions are not recommended.
 * 1. Blocking will stop the time wheel.
 * 2. Throwing an exception is intercepted.
 *
 * @version 1.0 2017-12-13 17:37:38
 * @param <T> target.
 * @since 1.5
 * @author dongbin
 */
@FunctionalInterface
public interface TimeoutNotification<T> {

    /**
     * Notification object expires.
     *
     * @param t target.
     * @return If it is greater than 0, it adds the target back into the ring in return time. Otherwise it does expire.
     */
    long notice(T t);
}
