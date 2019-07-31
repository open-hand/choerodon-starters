package io.choerodon.websocket.websocket.health;

import io.choerodon.websocket.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Default io.choerodon.websocket.websocket.health check implementation.
 *
 * After starting the health check, even if the message is not Pong's message, it will not be considered as the health check passed.
 *
 * @version 0.1 2019-07-26 17:33
 * @author dongbin
 * @since 1.8
 */
public class DefaultHealthCheck extends AbstractHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHealthCheck.class);

    private Map<String, CountDownLatch> checkingRoom;

    private LongAdder checkingNumber = new LongAdder();


    @PostConstruct
    @Override
    public void init() {
        super.init();
        checkingRoom = new ConcurrentHashMap<>();

    }

    @PreDestroy
    @Override
    public void destroy() {
        checkingRoom.clear();
        super.destroy();
    }

    @Override
    protected void onPong(Session session, PongMessage message) {
//        CountDownLatch latch = checkingRoom.get(session.getUuid());
//        if (latch != null) {
//
//            latch.countDown();
//
//        } else {
//
//            LOGGER.warn(
//                "The corresponding Session({}) receiving probe was not found when Pong message was received, may be timeout.",
//                session.toString());
//
//        }
    }

    /**
     * The quantity currently being checked.
     * @return size.
     */
    public int getCurrentCheckingSize() {
        return checkingNumber.intValue();
    }

    @Override
    protected boolean check(Session session) {
        if (session == null || session.getUuid() == null) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Try check for an invalid Session.");
            }

            return false;
        }

        CountDownLatch latch = new CountDownLatch(1);
        checkingRoom.put(session.getUuid(), latch);
        checkingNumber.increment();

        boolean result;


        try {

            session.getWebSocketSession().sendMessage(new PingMessage());

            result = await(latch, getProperties().getHealthCheckTimeout());


        } catch (IOException ex) {

            LOGGER.error(ex.getMessage(), ex);

            result = false;

        } finally {
            checkingRoom.remove(session.getUuid());
            checkingNumber.decrement();
        }

        return result;
    }

    @Override
    protected void eliminate(Session session) {
        try {
            session.getWebSocketSession().close(CloseStatus.GOING_AWAY);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private boolean await(CountDownLatch latch, long timeout) {
        long start = System.currentTimeMillis();
        long waitTimeoutMs = timeout;
        while (true) {
            try {

                return latch.await(waitTimeoutMs, TimeUnit.MILLISECONDS);

            } catch (InterruptedException ex) {

                long using = System.currentTimeMillis() - start;
                if (using < timeout) {

                    waitTimeoutMs = timeout - using;

                }
            }
        }
    }
}
