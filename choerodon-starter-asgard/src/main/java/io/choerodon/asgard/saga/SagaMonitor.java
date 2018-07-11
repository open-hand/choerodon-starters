package io.choerodon.asgard.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.saga.SagaDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SagaMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaMonitor.class);

    private ChoerodonSagaProperties choerodonSagaProperties;

    private Optional<EurekaRegistration> eurekaRegistration;

    private SagaClient sagaClient;

    private Executor executor;

    static final Map<String, SagaTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private DataSourceTransactionManager transactionManager;

    private ObjectMapper objectMapper = new ObjectMapper();

    static final Set<Long> processingIds = Collections.synchronizedSet(new HashSet<>());


    public SagaMonitor(ChoerodonSagaProperties choerodonSagaProperties,
                       SagaClient sagaClient,
                       Executor executor,
                       DataSourceTransactionManager transactionManager,
                       Optional<EurekaRegistration> eurekaRegistration) {
        this.choerodonSagaProperties = choerodonSagaProperties;
        this.sagaClient = sagaClient;
        this.executor = executor;
        this.eurekaRegistration = eurekaRegistration;
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    private void start() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (eurekaRegistration.isPresent()) {
                CloudEurekaInstanceConfig cloudEurekaInstanceConfig = eurekaRegistration.get().getInstanceConfig();
                if (cloudEurekaInstanceConfig instanceof EurekaInstanceConfigBean) {
                    EurekaInstanceConfigBean eurekaInstanceConfigBean = (EurekaInstanceConfigBean) cloudEurekaInstanceConfig;
                    String instance = eurekaInstanceConfigBean.getIpAddress() + ":" + eurekaInstanceConfigBean.getNonSecurePort();
                    Observable.from(getSagaTasks(instance))
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(Schedulers.from(executor))
                            .subscribe(new Observer<DataObject.SagaTaskInstanceDTO>() {
                                @Override
                                public void onCompleted() {
                                    // onCompleted
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LOGGER.warn("error.invokeSagaTaskInstance {}", e.getMessage());
                                }

                                @Override
                                public void onNext(DataObject.SagaTaskInstanceDTO sagaTaskInstanceDTO) {
                                    invoke(sagaTaskInstanceDTO);
                                }
                            });
                }
            }
        }, 20, choerodonSagaProperties.getPollInterval(), TimeUnit.SECONDS);
    }

    private void invoke(DataObject.SagaTaskInstanceDTO data) {
        final String key = data.getSagaCode() + data.getTaskCode();
        SagaTaskInvokeBean invokeBean = invokeBeanMap.get(key);
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
            processingIds.remove(data.getId());
        } catch (Exception e) {
            processingIds.remove(data.getId());
            transactionManager.rollback(status);
            sagaClient.updateStatus(data.getId(), new DataObject.SagaTaskInstanceStatusDTO(data.getId(),
                    SagaDef.InstanceStatus.STATUS_FAILED.name(), getErrorInfoFromException(e)));
            LOGGER.warn("message consume exception, msg : {}, cause {}", data, e.getMessage());
            if (LOGGER.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
    }

    private String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

    private List<DataObject.SagaTaskInstanceDTO> getSagaTasks(final String instance) {
        List<DataObject.SagaTaskInstanceDTO> list = new ArrayList<>();
        try {
            invokeBeanMap.forEach((k, v) -> {
                List<DataObject.SagaTaskInstanceDTO> taskList = sagaClient.pollBatch(v.sagaTask.code(), instance, processingIds);
                list.addAll(taskList);
            });
            list.forEach(t -> processingIds.add(t.getId()));
        } catch (Exception e) {
            LOGGER.info("error.pollSagaTaskInstances {}", e.getMessage());
        }
        LOGGER.info("poll sagaTaskInstances from asgard, time {} instance {} size {}", System.currentTimeMillis(), instance, list.size());
        return list;
    }
}
