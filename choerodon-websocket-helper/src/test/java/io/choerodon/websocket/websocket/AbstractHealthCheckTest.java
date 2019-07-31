package io.choerodon.websocket.websocket;

import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.websocket.health.AbstractHealthCheck;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AbstractHealthCheck Tester.
 *
 * @author dongbin
 * @version 1.0
 * @since 1.8
 */
public class AbstractHealthCheckTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHealthCheckTest.class);

    private static SocketProperties socketProperties;
    private MockHealthCheckImpl impl;

    @BeforeClass
    public static void beforeClass() throws Exception {
        socketProperties = new SocketProperties();
    }

    @Before
    public void before() throws Exception {

        impl = new MockHealthCheckImpl();
        // injuct
        ReflectionTestUtils.setField(impl, "properties", socketProperties, SocketProperties.class);

    }

    @After
    public void after() throws Exception {
        impl.destroy();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        socketProperties = null;
    }

    /**
     *
     * Tests a Session specified number of times to see if it is still valid after expiration.
     */
    @Test
    public void testHealthCheckNoPass() throws Exception {
        impl.setSessionCheckFunc(s -> false);


        socketProperties.setHealthCheckDuration(200);


        impl.init();

        Session s = buildMockSession();
        impl.onCreated(s);

        waitEmpty(impl);

        List<Long> checkTime = impl.getCheckTimes(s);
        LOGGER.info("Check time series {}", checkTime);



        Assert.assertEquals(socketProperties.getHealthCheckTryNumber(), checkTime.size());
        Assert.assertTrue(impl.getEliminateSession().size() == 1);
        Assert.assertEquals(s, impl.getEliminateSession().get(0));
    }


    /**
     * Tests that close sessions early should not be expected to trigger elimination rules.
     * There should also be no io.choerodon.websocket.websocket.health checks.
     */
    @Test
    public void testSessionAdvanceClose() throws Exception {
        final AtomicInteger checkNumber = new AtomicInteger(0);
        impl.setSessionCheckFunc(s -> {
            checkNumber.incrementAndGet();
            return true;
        });

        socketProperties.setHealthCheckDuration(300);
        impl.init();

        Session s = buildMockSession();
        impl.onCreated(s);

        TimeUnit.MILLISECONDS.sleep(100);

        impl.onClosed(s);

        Assert.assertTrue(impl.size() == 0);
        Assert.assertEquals(0, checkNumber.intValue());
        Assert.assertTrue(impl.getEliminateSession().isEmpty());
    }

    /**
     * If the Session receives a message, the test updates its next check point,
     * and no io.choerodon.websocket.websocket.health check is expected.
     * If no message arrives, the test is expected to check the tryNumber + 1.
     */
    @Test
    public void testSessionRecviceMessage() throws Exception {
        final AtomicInteger checkNumber = new AtomicInteger(0);
        impl.setSessionCheckFunc(s -> {
            checkNumber.incrementAndGet();
            if (checkNumber.intValue() > 1) {
                return false;
            }
            return true;
        });

        socketProperties.setHealthCheckDuration(300);

        impl.init();

        Session s = buildMockSession();
        impl.onCreated(s);

        for (int i = 0; i < 10; i++) {
            impl.onReceived(s, null);
            TimeUnit.MILLISECONDS.sleep(100);
        }

        impl.onClosed(s);

        Assert.assertTrue(impl.size() == 0);
        Assert.assertEquals(0, checkNumber.intValue());
        Assert.assertTrue(impl.getEliminateSession().isEmpty());

        checkNumber.set(0);
        s = buildMockSession();
        impl.onCreated(s);

        waitEmpty(impl);

        Assert.assertEquals(1 + socketProperties.getHealthCheckTryNumber(), checkNumber.intValue());
        Assert.assertEquals(1, impl.getEliminateSession().size());
        Assert.assertEquals(s, impl.getEliminateSession().get(0));
    }



    private Session buildMockSession() {
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSession.getAttributes()).thenReturn(new HashMap() {{
            put("key", UUID.randomUUID().toString());
        }});
        when(webSocketSession.getRemoteAddress()).thenReturn(new InetSocketAddress("localhost", 8206));

        Session s = new Session(webSocketSession, true);
        return s;
    }


    /**
     * mock
     */
    static class MockHealthCheckImpl extends AbstractHealthCheck {

        private Predicate<Session> sessionCheckFunc;
        private Map<Session, List<Long>> checkTimeLog;
        private List<Session> eliminateSession;

        public MockHealthCheckImpl() {

            this.checkTimeLog = new HashMap<>();

            this.eliminateSession = new ArrayList<>();
        }

        public void setSessionCheckFunc(Predicate<Session> sessionCheckFunc) {
            this.sessionCheckFunc = sessionCheckFunc;
        }

        @Override
        protected boolean check(Session session) {
            synchronized (session) {
                List<Long> checkPoint = checkTimeLog.get(session);
                if (checkPoint == null) {
                    checkPoint = new ArrayList();
                    checkTimeLog.put(session, checkPoint);
                }
                checkPoint.add(System.currentTimeMillis());
            }
            return this.sessionCheckFunc.test(session);
        }

        @Override
        protected void eliminate(Session session) {
            eliminateSession.add(session);
        }

        @Override
        public void onPong(Session session, PongMessage pongMessage) {

        }

        public List<Long> getCheckTimes(Session session) {
            return checkTimeLog.get(session);
        }

        public List<Session> getEliminateSession() {
            return eliminateSession;
        }
    }

    private void waitEmpty(MockHealthCheckImpl impl) throws InterruptedException {
        final int maxTimes = 100;
        int size;
        int times = 0;
        while(true) {
            if (times >= maxTimes) {
                break;
            }

            size = impl.size();
            if (size > 0) {
                LOGGER.info("{} tasks, wait 1 second.", size);
                times++;
                TimeUnit.SECONDS.sleep(1);
            } else {
                break;
            }
        }
    }

} 
