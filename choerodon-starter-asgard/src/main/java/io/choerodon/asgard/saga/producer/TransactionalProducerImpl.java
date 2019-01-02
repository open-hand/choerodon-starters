package io.choerodon.asgard.saga.producer;

import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.consistency.SagaProducerConsistencyHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.transaction.TransactionDefinition.ISOLATION_DEFAULT;


public class TransactionalProducerImpl implements TransactionalProducer {

    private PlatformTransactionManager transactionManager;

    private SagaProducerConsistencyHandler consistencyHandler;

    private SagaClient sagaClient;

    public TransactionalProducerImpl(PlatformTransactionManager transactionManager,
                                     SagaProducerConsistencyHandler consistencyHandler,
                                     SagaClient sagaClient) {
        this.transactionManager = transactionManager;
        this.consistencyHandler = consistencyHandler;
        this.sagaClient = sagaClient;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }


    @Override
    public <T> T applyAndReturn(final StartSagaBuilder builder, final Function<StartSagaBuilder, T> function) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(ISOLATION_DEFAULT);
        return applyAndReturn(builder, function, def);
    }

    @Override
    public void apply(final StartSagaBuilder builder, final Consumer<StartSagaBuilder> consumer) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(ISOLATION_DEFAULT);
        apply(builder, consumer);
    }

    @Override
    public <T> T applyAndReturn(StartSagaBuilder builder,
                                Function<StartSagaBuilder, T> function,
                                TransactionDefinition definition) {
        T result;
        String uuid = generateUUID();
        TransactionStatus status = transactionManager.getTransaction(definition);
        builder.withUuid(uuid);
        try {
            sagaClient.preCreateSaga(builder.preBuild());
            result = function.apply(builder);
            consistencyHandler.beforeTransactionCommit(uuid);
            transactionManager.commit(status);
        } catch (Exception e) {
            consistencyHandler.beforeTransactionCancel(uuid);
            transactionManager.rollback(status);
            sagaClient.cancelSaga(uuid);
            throw e;
        }
        sagaClient.confirmSaga(uuid, builder.getPayloadJson());
        return result;
    }

    @Override
    public void apply(StartSagaBuilder builder,
                      Consumer<StartSagaBuilder> consumer,
                      TransactionDefinition definition) {
        String uuid = generateUUID();
        TransactionStatus status = transactionManager.getTransaction(definition);
        builder.withUuid(uuid);
        try {
            sagaClient.preCreateSaga(builder.preBuild());
            consumer.accept(builder);
            consistencyHandler.beforeTransactionCommit(uuid);
            transactionManager.commit(status);
        } catch (Exception e) {
            consistencyHandler.beforeTransactionCancel(uuid);
            transactionManager.rollback(status);
            sagaClient.cancelSaga(uuid);
            throw e;
        }
        sagaClient.confirmSaga(uuid, builder.getPayloadJson());
    }
}
