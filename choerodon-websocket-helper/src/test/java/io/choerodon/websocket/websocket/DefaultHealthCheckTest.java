package io.choerodon.websocket.websocket;

import io.choerodon.websocket.session.Session;
import io.choerodon.websocket.websocket.health.DefaultHealthCheck;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * DefaultHealthCheck Tester.
 *
 * @author dongbin
 * @version 1.0 07/29/2019
 * @since 1.8
 */
public class DefaultHealthCheckTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHealthCheckTest.class);

    private static SocketProperties socketProperties;

    private DefaultHealthCheck impl;

    @BeforeClass
    public static void beforeClass() throws Exception {
        socketProperties = new SocketProperties();
    }

    @Before
    public void before() throws Exception {

        impl = new DefaultHealthCheck();

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
     * It is similar to ping-pong heartbeat and is expected to pass io.choerodon.websocket.websocket.health examination.
     */
    @Test
    public void testRecvicePongMessage() throws Exception {
        socketProperties.setHealthCheckDuration(300);
        // Unlimited wait for io.choerodon.websocket.websocket.health check.
        socketProperties.setHealthCheckTimeout(Integer.MAX_VALUE);
        impl.init();

        Session s = buildMockSession(false);
        impl.onCreated(s);

        TimeUnit.MILLISECONDS.sleep(450);

        Assert.assertEquals(1, impl.getCurrentCheckingSize());

        impl.onReceived(s, new PongMessage());

        TimeUnit.MILLISECONDS.sleep(100);

        Assert.assertEquals(0, impl.getCurrentCheckingSize());
        Assert.assertEquals(1, impl.size());
    }

    /**
     * All attempts are expected to be exhausted, and each timeout is correctly eliminated.
     */
    @Test
    public void testCheckTimeout() throws Exception {
        socketProperties.setHealthCheckDuration(100);
        // 2 ms timeout
        socketProperties.setHealthCheckTimeout(2);
        impl.init();

        Session s = buildMockSession(false);
        impl.onCreated(s);


        waitEmpty(impl);

        Assert.assertEquals(0, impl.getCurrentCheckingSize());
        Assert.assertEquals(0, impl.size());

    }

    private Session buildMockSession(boolean error) throws Exception {
        WebSocketSession webSocketSession = mock(WebSocketSession.class);
        when(webSocketSession.getAttributes()).thenReturn(new HashMap() {{
            put("key", UUID.randomUUID().toString());
            put("SESSION_ID", UUID.randomUUID().toString());
        }});
        when(webSocketSession.getRemoteAddress()).thenReturn(new InetSocketAddress("localhost", 8206));

        if (error) {
            doThrow(new IOException()).when(webSocketSession).sendMessage(new PingMessage());
        }

        return new Session(webSocketSession, true);
    }

    private void waitEmpty(DefaultHealthCheck impl) throws InterruptedException {
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
