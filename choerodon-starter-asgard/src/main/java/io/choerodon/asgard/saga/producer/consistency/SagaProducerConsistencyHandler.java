package io.choerodon.asgard.saga.producer.consistency;

import io.choerodon.asgard.saga.dto.SagaStatusQueryDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 保证数据一致性的处理器
 */
public abstract class SagaProducerConsistencyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaProducerConsistencyHandler.class);

    private static final long CLEAR_INTERVAL = 86_400_000L;

    SagaProducerConsistencyHandler(ScheduledExecutorService executorService) {
        executorService.scheduleWithFixedDelay(() -> {
            try {
                clear(CLEAR_INTERVAL);
            } catch (Exception e) {
                LOGGER.warn("error.sagaProducerConsistencyHandle.scheduleClear", e);
            }
        }, 2, 60, TimeUnit.MINUTES);
    }

    public abstract void beforeTransactionCommit(String uuid, StartInstanceDTO dto);

    public abstract void beforeTransactionCancel(String uuid);

    /**
     * 提供给choerodon-asgard回查使用
     *
     * @param uuid 回查的id
     * @return SagaStatusQueryDTO
     */
    public abstract SagaStatusQueryDTO asgardServiceBackCheck(String uuid);


    /**
     * 清除老旧的数据
     *
     * @param time 距现在多久之前的数据将被清除(单位，毫秒)
     */
    abstract void clear(long time);

}
