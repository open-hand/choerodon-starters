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

    private String service;

    public TransactionalProducerImpl(PlatformTransactionManager transactionManager,
                                     SagaProducerConsistencyHandler consistencyHandler,
                                     SagaClient sagaClient,
                                     String service) {
        this.transactionManager = transactionManager;
        this.consistencyHandler = consistencyHandler;
        this.sagaClient = sagaClient;
        this.service = service;
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
        apply(builder, consumer, def);
    }

    @Override
    public <T> T applyAndReturn(StartSagaBuilder builder,
                                Function<StartSagaBuilder, T> function,
                                TransactionDefinition definition) {
        T result;
        String uuid = generateUUID();
        TransactionStatus status = transactionManager.getTransaction(definition);
        builder.withUuid(uuid).withService(service);
        try {
            // 解决TransactionalProducer.applyAndReturn() function中设置 sourceId不生效问题
            result = function.apply(builder);
            sagaClient.preCreateSaga(builder.preBuild());
            consistencyHandler.beforeTransactionCommit(uuid, builder.confirmBuild());
            transactionManager.commit(status);
        } catch (Exception e) {
            consistencyHandler.beforeTransactionCancel(uuid);
            transactionManager.rollback(status);
            sagaClient.cancelSaga(uuid);
            throw e;
        }
        sagaClient.confirmSaga(uuid, builder.confirmBuild());
        return result;
    }

    @Override
    public void apply(StartSagaBuilder builder,
                      Consumer<StartSagaBuilder> consumer,
                      TransactionDefinition definition) {
        String uuid = generateUUID();
        TransactionStatus status = transactionManager.getTransaction(definition);
        builder.withUuid(uuid).withService(service);
        try {
            sagaClient.preCreateSaga(builder.preBuild());
            consumer.accept(builder);
            consistencyHandler.beforeTransactionCommit(uuid, builder.confirmBuild());
            transactionManager.commit(status);
        } catch (Exception e) {
            consistencyHandler.beforeTransactionCancel(uuid);
            transactionManager.rollback(status);
            sagaClient.cancelSaga(uuid);
            throw e;
        }
        sagaClient.confirmSaga(uuid, builder.confirmBuild());
    }
}
