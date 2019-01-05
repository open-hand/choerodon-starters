package io.choerodon.asgard.saga.producer.consistency;

import io.choerodon.asgard.saga.dto.SagaStatusQueryDTO;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import static io.choerodon.asgard.saga.dto.SagaStatusQueryDTO.STATUS_CANCEL;
import static io.choerodon.asgard.saga.dto.SagaStatusQueryDTO.STATUS_CONFIRM;

/**
 * 基于内存实现的回查方式
 */
public class SagaProducerMemoryConsistencyHandler extends SagaProducerConsistencyHandler {

    private final Map<String, StatusCache> uuidTimeMap = new ConcurrentHashMap<>();

    public SagaProducerMemoryConsistencyHandler(ScheduledExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void beforeTransactionCommit(String uuid, StartInstanceDTO dto) {
        uuidTimeMap.put(uuid, new StatusCache(System.currentTimeMillis(), dto.getInput(), dto.getRefType(), dto.getRefId()));
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        uuidTimeMap.remove(uuid);
    }

    @Override
    public SagaStatusQueryDTO asgardServiceBackCheck(String uuid) {
        StatusCache cache = uuidTimeMap.get(uuid);
        if (cache == null) {
            return new SagaStatusQueryDTO(STATUS_CANCEL);
        } else {
            return new SagaStatusQueryDTO(STATUS_CONFIRM, cache.payload, cache.refType, cache.refId);
        }
    }

    @Override
    public void clear(long time) {
        Iterator<Map.Entry<String, StatusCache>> it = uuidTimeMap.entrySet().iterator();
        long current = System.currentTimeMillis();
        while (it.hasNext()) {
            Map.Entry<String, StatusCache> entry = it.next();
            if (entry.getValue().time + time < current) {
                it.remove();
            }
        }
    }

    private static class StatusCache {
        final long time;
        final String payload;
        final String refType;
        final String refId;

        StatusCache(long time, String payload, String refType, String refId) {
            this.time = time;
            this.payload = payload;
            this.refType = refType;
            this.refId = refId;
        }
    }
}
