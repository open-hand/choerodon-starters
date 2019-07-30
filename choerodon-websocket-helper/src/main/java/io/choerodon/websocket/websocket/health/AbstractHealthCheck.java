package io.choerodon.websocket.websocket.health;

import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.tool.ThreadTool;
import io.choerodon.websocket.websocket.SocketProperties;
import io.choerodon.websocket.websocket.health.utils.TimeoutNotification;
import io.choerodon.websocket.websocket.health.utils.TimerWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketMessage;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provide the implementation of io.choerodon.websocket.websocket.health examination. Define the specific framework of io.choerodon.websocket.websocket.health examination.
 * The implementation USES an algorithm based on "time slice transformation" to manage each Session without
 * receiving a message for a specified period of time.
 *
 * @author dongbin
 * @version 0.1 2019-07-26 14:04
 * @since 1.8
 */
public abstract class AbstractHealthCheck implements HealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHealthCheck.class);

    private TimerWheel<Session> timeWheel;

    @Resource
    private SocketProperties properties;

    private ExecutorService healthCheckWorker;

    public void init() {

        LOGGER.info("Initialize health check({}) with initialization parameter ({}).",this.getClass(), healthCheckParam());
        /**
         * The default value is sufficient for the usage scenario.
         * Unless the number of agents is too large, consider adjusting the parameters here.
         */
        timeWheel = new TimerWheel<>(new SessionTimeoutNotification());

        // Background thread pools do not need to be explicitly closed.
        healthCheckWorker = new ThreadPoolExecutor(
            properties.getHealthCheckWorkerNumber(),
            properties.getHealthCheckWorkerNumber(),
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            ThreadTool.buildNameThreadFactory("Health check doctor", true));

    }

    public void destroy() {
        // give up all check task.
        healthCheckWorker.shutdownNow();
        timeWheel.stop();

        LOGGER.info("Termination of health check({})!",this.getClass());
    }

    @Override
    public void onCreated(Session session) {
        timeWheel.add(session, this.properties.getHealthCheckDuration());
    }

    @Override
    public void onReceived(Session session, WebSocketMessage msg) {
        session.setLastReceive(System.currentTimeMillis());

        if (PongMessage.class.isInstance(msg)) {
            onPong(session, (PongMessage) msg);
        }
    }

    @Override
    public void onClosed(Session session) {
        timeWheel.remove(session);
    }

    /**
     * Gives the number of sessions currently waiting to be checked.
     *
     * @return number.
     */
    public int size() {
        return timeWheel.size();
    }

    /**
     * get now socket properties.
     * @return properteis.
     */
    public SocketProperties getProperties() {
        return properties;
    }

    private String healthCheckParam() {
        Field[] fields = properties.getClass().getDeclaredFields();
        StringBuilder buff = new StringBuilder();
        buff.append("[");
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.getName().startsWith("healthCheck")) {
                try {
                    buff.append(f.getName()).append("=").append(f.get(properties)).append(",");
                } catch(Exception ex) {
                    throw new RuntimeException(ex.getMessage(),ex);
                }
            }
        }
        buff.append("]");

        return buff.toString();
    }

    /**
     * Processing when a Ping Pong response is received.
     * @param session target session.
     * @param message target message.
     */
    protected abstract void onPong(Session session, PongMessage message);

    /**
     * Actually check.
     *
     * @param session target.
     * @return true io.choerodon.websocket.websocket.health, false unhealthy.
     */
    protected abstract boolean check(Session session);

    /**
     * Eliminate the specified Session.
     *
     * @param session target.
     */
    protected abstract void eliminate(Session session);


    /**
     * This is the main logic for notifying when the time round finds that the specified Session arrives at the scheduled time.
     */
    private class SessionTimeoutNotification implements TimeoutNotification<Session> {

        @Override
        public long notice(final Session session) {

            if (needCheck(session)) {
                // Notification callbacks that cannot block time slices.
                healthCheckWorker.submit(() -> {
                    try {

                        if (session.getHealthCheckTriedTimes() >= properties.getHealthCheckTryNumber()) {

                            eliminate(session);

                        } else {

                            if (check(session)) {

                                session.setHealthCheckTriedTimes(0);
                                timeWheel.add(session, properties.getHealthCheckDuration());

                            } else {

                                if (session.getHealthCheckTriedTimes() < properties.getHealthCheckTryNumber()) {

                                    session.setHealthCheckTriedTimes(session.getHealthCheckTriedTimes() + 1);
                                    timeWheel.add(session, properties.getHealthCheckDuration());

                                } else {

                                    eliminate(session);

                                }

                            }
                        }

                    } catch (Throwable ex) {

                        LOGGER.error(ex.getMessage(), ex);

                        eliminate(session);

                    }
                });

                /**
                 * This keeps returning 0, indicating that the instance needs to be deleted,
                 * because the extra thread will decide whether to rejoin the ring based on the check.
                 */
                return 0;

            } else {

                // Since the message was received again during the wait time, rejoin the wheel and retime.
                return properties.getHealthCheckDuration();
            }

        }

        private boolean needCheck(Session session) {
            return System.currentTimeMillis() - session.getLastReceive() >= properties.getHealthCheckDuration();
        }
    }
}
