package io.choerodon.asgard.saga;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RxjavaTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RxjavaTest.class);

    @Test
    public void flatMapTest() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("saga-service-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        Arrays.asList(1, 2, 3, 4, 5).forEach(t -> {
            executor.execute(() -> {
                try {
                    CountDownLatch latch = new CountDownLatch(1);
                    Observable.interval(3, TimeUnit.SECONDS)
                            .flatMap((Long aLong) -> Observable.from(pollBatch()))
                            .observeOn(Schedulers.from(executor))
                            .distinct()
                            .subscribe((DataObject.SagaTaskInstanceDTO taskInstanceDTO) -> {
                                LOGGER.info(taskInstanceDTO.getId() + " thread: "
                                        + Thread.currentThread().getId() + " name " + Thread.currentThread().getName());
                            });
                    latch.await();
                } catch (InterruptedException e) {
                    LOGGER.error("error.sagaPollThread.stop {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            });
        });

        try {
            Thread.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    List<DataObject.SagaTaskInstanceDTO> pollBatch() {
        System.out.println(Thread.currentThread().getId() + " name " + Thread.currentThread().getName());
        System.out.println();
        return Arrays.asList(new DataObject.SagaTaskInstanceDTO((long) (Math.random() * 10) + 1L),
                new DataObject.SagaTaskInstanceDTO((long) (Math.random() * 10) + 1L));
    }
}
