package io.choerodon.asgard.saga.producer.consistency;

/**
 * 基于数据表实现的回查方式
 */
public class SagaProducerDbConsistencyHandler implements SagaProducerConsistencyHandler {

    private SagaProducerStore sagaProducerStore;

    public SagaProducerDbConsistencyHandler(SagaProducerStore sagaProducerStore) {
        this.sagaProducerStore = sagaProducerStore;
    }

    @Override
    public void beforeTransactionCommit(String uuid) {
        sagaProducerStore.record(uuid);
    }

    @Override
    public void beforeTransactionCancel(String uuid) {
        // do nothing
    }

    @Override
    public String asgardServiceBackCheck(String uuid) {
        return sagaProducerStore.selectByUUID(uuid);
    }

    @Override
    public void clear(long time) {
        sagaProducerStore.clear(time);
    }
}
