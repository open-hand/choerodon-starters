package io.choerodon.asgard.saga.producer;


import org.springframework.transaction.TransactionDefinition;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionalProducer {

    <T> T applyAndReturn(final StartSagaBuilder builder,
                         final Function<StartSagaBuilder, T> function);

    <T> T applyAndReturn(final StartSagaBuilder builder,
                         final Function<StartSagaBuilder, T> function,
                         final TransactionDefinition definition);

    void apply(final StartSagaBuilder builder,
               final Consumer<StartSagaBuilder> consumer);

    void apply(final StartSagaBuilder builder,
               final Consumer<StartSagaBuilder> consumer,
               final TransactionDefinition definition);

}
