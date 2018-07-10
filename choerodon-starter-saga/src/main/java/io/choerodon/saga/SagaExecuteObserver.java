package io.choerodon.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.saga.SagaDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import rx.Subscriber;

import java.util.HashMap;
import java.util.Map;

public class SagaExecuteObserver extends Subscriber<DataObject.SagaTaskInstanceDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaExecuteObserver.class);
    static final Map<String, SagaTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private DataSourceTransactionManager transactionManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    private SagaClient sagaClient;

    public SagaExecuteObserver(DataSourceTransactionManager transactionManager, SagaClient sagaClient) {
        this.transactionManager = transactionManager;
        this.sagaClient = sagaClient;
    }

    @Override
    public void onCompleted() {
        // do
    }

    @Override
    public void onError(Throwable throwable) {
        // do
    }

    @Override
    public void onNext(DataObject.SagaTaskInstanceDTO taskInstance) {
        final String key = taskInstance.getSagaCode() + taskInstance.getTaskCode();
        SagaTaskInvokeBean invokeBean = invokeBeanMap.get(key);
        execute(invokeBean, taskInstance);
    }

    private void execute(final SagaTaskInvokeBean invokeBean, final DataObject.SagaTaskInstanceDTO data) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(invokeBean.sagaTask.transactionDefinition());
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            invokeBean.method.setAccessible(true);
            Object result = invokeBean.method.invoke(invokeBean.object, data.getInputData());
            String resultData = null;
            if (result != null) {
                resultData = objectMapper.writeValueAsString(result);
            }
            sagaClient.updateStatus(data.getId(), new DataObject.SagaTaskInstanceStatusDTO(data.getId(),
                    SagaDef.InstanceStatus.STATUS_COMPLETED.name(), resultData));
            transactionManager.commit(status);
        } catch (Exception e) {
            LOGGER.warn("message consume exception, msg : {}, cause {}", data, e);
            if (LOGGER.isDebugEnabled()) {
                e.printStackTrace();
            }
            transactionManager.rollback(status);
        }
    }


}
