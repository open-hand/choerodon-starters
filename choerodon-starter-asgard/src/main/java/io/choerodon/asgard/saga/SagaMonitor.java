package io.choerodon.asgard.saga;

import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SagaMonitor {

    private ChoerodonSagaProperties choerodonSagaProperties;

    private Optional<EurekaRegistration> eurekaRegistration;

    private SagaClient sagaClient;

    private Executor executor;

    private SagaExecuteObserver observer;


    public SagaMonitor(ChoerodonSagaProperties choerodonSagaProperties,
                       SagaClient sagaClient,
                       Executor executor,
                       SagaExecuteObserver observer,
                       Optional<EurekaRegistration> eurekaRegistration) {
        this.choerodonSagaProperties = choerodonSagaProperties;
        this.sagaClient = sagaClient;
        this.executor = executor;
        this.observer = observer;
        this.eurekaRegistration = eurekaRegistration;
    }

    @PostConstruct
    public void start() {
        if (eurekaRegistration.isPresent()) {
            CloudEurekaInstanceConfig cloudEurekaInstanceConfig = eurekaRegistration.get().getInstanceConfig();
            if (cloudEurekaInstanceConfig instanceof EurekaInstanceConfigBean) {
                EurekaInstanceConfigBean eurekaInstanceConfigBean = (EurekaInstanceConfigBean) cloudEurekaInstanceConfig;
                String instance = eurekaInstanceConfigBean.getInstanceId();
                SagaExecuteObserver.invokeBeanMap.entrySet().forEach(i ->
                        Observable.interval(choerodonSagaProperties.getPollInterval(), TimeUnit.SECONDS)
                                .flatMap((Long aLong) -> Observable.from(sagaClient.pollBatch(i.getValue().sagaTask.code(), instance, null)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.from(executor))
                                .distinct()
                                .subscribe(observer)
                );
            }
        }
    }
}
