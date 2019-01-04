package io.choerodon.asgard.saga.producer.consistency;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于内存实现的回查方式
 */
public class SagaProducerMemoryConsistencyHandler implements SagaProducerConsistencyHandler {

    private final ConcurrentMap<String, Long> uuidTimeMap = new ConcurrentHashMap<>();

    @Override
    public void beforeTransactionCommit(String uuid) {
        uuidTimeMap.put(uuid, System.currentTimeMillis());
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        uuidTimeMap.remove(uuid);
    }

    @Override
    public String asgardServiceBackCheck(String uuid) {
        if (uuidTimeMap.containsKey(uuid)) {
            return uuid;
        }
        return null;
    }

    @Override
    public void clear(long time) {
        Iterator<Map.Entry<String, Long>> it = uuidTimeMap.entrySet().iterator();
        long current = System.currentTimeMillis();
        while (it.hasNext()) {
            Map.Entry<String, Long> entry = it.next();
            if (entry.getValue() + time < current) {
                it.remove();
            }
        }
    }
}
