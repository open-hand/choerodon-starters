package io.choerodon.websocket.websocket.utils;

import io.choerodon.websocket.websocket.health.utils.TimeoutNotification;
import io.choerodon.websocket.websocket.health.utils.TimerWheel;
import org.junit.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Time slice rotation algorithm is tested.
 * @version 1.0 2019-07-26 14:04
 * @since 1.5
 * @author dongbin
 */
public class TimerWheelTest {

    public TimerWheelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * The test has only one expired object and will expire after 5 seconds with an error of 1 second.
     */
    @Test
    public void testAddValue() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger result = new AtomicInteger(0);
        TimerWheel wheel = new TimerWheel(new TimeoutNotification<Long>() {
            @Override
            public long notice(Long addTime) {
                try {
                    long expireTime = System.currentTimeMillis();

                    long space = expireTime - addTime;
                    if (space <= 6000) {//6000是因为wheel不是一个绝对准确的实现,所以终止时间会有误差.
                        result.incrementAndGet();
                    }

                    return 0;

                } finally {
                    latch.countDown();
                }
            }
        });

        wheel.add(System.currentTimeMillis(), 5000);
        latch.await();

        Assert.assertEquals(1, result.get());
        Assert.assertTrue(wheel.size() == 0);
    }

    /**
     * After the test is eliminated, write the loop again directly in the callback.
     */
    @Test
    public void testExpireAddValue() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger result = new AtomicInteger(0);
        TimerWheel wheel = new TimerWheel(new TimeoutNotification<Long>() {
            @Override
            public long notice(Long addTime) {
                try {
                    long expireTime = System.currentTimeMillis();

                    long space = expireTime - addTime;
                    // 6000 is because wheel is not an absolutely accurate implementation, so there will be errors in the termination time.
                    if (result.get() == 0 && space <= 6000) {
                        result.incrementAndGet();
                    } else if (result.get() == 1 && space <= 12000) {
                        result.incrementAndGet();
                    }

                    if (result.get() == 1) {
                        return 5000;
                    } else {
                        return 0;
                    }

                } finally {
                    if (result.get() == 2) {
                        latch.countDown();
                    }
                }
            }
        });

        wheel.add(System.currentTimeMillis(), 5000);
        latch.await();

        Assert.assertEquals(2, result.get());
        Assert.assertTrue(wheel.size() == 0);
    }
    
    /**
     * Do not eliminate, active delete.
     * @throws Exception 
     */
    @Test
    public void testRemoveValue() throws Exception {
        TimerWheel wheel = new TimerWheel(new TimeoutNotification() {
            @Override
            public long notice(Object t) {
                Assert.fail("Unexpected elimination can be known.");
                
                return 0;
            }
        });
        
        Object target = new Object();
        wheel.add(target, 5000);
        
        Assert.assertEquals(1, wheel.size());
        wheel.remove(target);
        Assert.assertEquals(0, wheel.size());
    }

    @Test
    public void testMultithreadingAddValue() throws Exception {
        int scale = 30;

        final CountDownLatch latch = new CountDownLatch(scale);
        TimerWheel wheel = new TimerWheel(t -> {
            try {

                return 0;

            } finally {
                latch.countDown();
            }
        });
        ExecutorService worker = Executors.newFixedThreadPool(scale);
        for (int i = 0; i < scale; i++) {
            worker.submit(() -> {
                wheel.add(new Object(), 500);
            });
        }

        latch.await();
        worker.shutdown();

        Assert.assertTrue(wheel.size() == 0);
    }

}
