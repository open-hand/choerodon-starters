package io.choerodon.asgard.schedule;


import io.choerodon.asgard.schedule.feign.ScheduleMonitorClient;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class ScheduleMonitor {

    private static final Map<String, JobTaskInvokeBean> invokeBeanMap = new HashMap<>();

    private final DataSourceTransactionManager transactionManager;

    private final Environment environment;

    private final Executor executor;

    private final ScheduleMonitorClient scheduleMonitorClient;

    public static Map<String, JobTaskInvokeBean> getInvokeBeanMap() {
        return invokeBeanMap;
    }

    static void addInvokeBean(String key, JobTaskInvokeBean  invokeBean) {
      invokeBeanMap.put(key, invokeBean);
    }

    public ScheduleMonitor(DataSourceTransactionManager transactionManager, Environment environment,
                           Executor executor, ScheduleMonitorClient scheduleMonitorClient) {
        this.transactionManager = transactionManager;
        this.environment = environment;
        this.executor = executor;
        this.scheduleMonitorClient = scheduleMonitorClient;
    }
}
