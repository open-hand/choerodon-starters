package io.choerodon.websocket.tool;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread help tool.
 * @version 0.1 2019-07-26 16:18
 * @auth dongbin
 * @since 1.8
 */
public class ThreadTool {

    /**
     * Create custom thread factories that support custom names and thread types.
     * Thread names are numbered at the end.
     *
     * @param name thread name.
     * @param daemon true daemon ,false not.
     * @return ThreadFactory instance.
     */
    public static ThreadFactory buildNameThreadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {

            private final AtomicLong number = new AtomicLong();

            @Override
            public Thread newThread(Runnable r) {
                Thread newThread = Executors.defaultThreadFactory().newThread(r);
                newThread.setName(name + "-" + number.getAndIncrement());
                newThread.setDaemon(daemon);
                return newThread;
            }

        };
    }
}
