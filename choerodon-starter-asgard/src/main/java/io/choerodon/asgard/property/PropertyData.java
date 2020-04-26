package io.choerodon.asgard.property;

import java.util.ArrayList;
import java.util.List;

public class PropertyData {

    private String service;

    private List<PropertySaga> sagas = new ArrayList<>();

    private List<PropertySagaTask> sagaTasks = new ArrayList<>();

    private List<PropertyJobTask> jobTasks = new ArrayList<>();

    private List<PropertyTimedTask> timedTasks = new ArrayList<>();

    void addSaga(PropertySaga saga) {
        this.sagas.add(saga);
    }

    void addSagaTask(PropertySagaTask sagaTask) {
        this.sagaTasks.add(sagaTask);
    }

    void addJobTask(PropertyJobTask jobTask) {
        this.jobTasks.add(jobTask);
    }

    void addTimedTasks(PropertyTimedTask timedTask) {
        this.timedTasks.add(timedTask);
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public List<PropertySaga> getSagas() {
        return sagas;
    }


    public List<PropertySagaTask> getSagaTasks() {
        return sagaTasks;
    }


    public List<PropertyJobTask> getJobTasks() {
        return jobTasks;
    }


    public List<PropertyTimedTask> getTimedTasks() {
        return timedTasks;
    }

    public void setSagas(List<PropertySaga> sagas) {
        this.sagas = sagas;
    }

    public void setSagaTasks(List<PropertySagaTask> sagaTasks) {
        this.sagaTasks = sagaTasks;
    }

    public void setJobTasks(List<PropertyJobTask> jobTasks) {
        this.jobTasks = jobTasks;
    }

    public void setTimedTasks(List<PropertyTimedTask> timedTasks) {
        this.timedTasks = timedTasks;
    }

    @Override
    public String toString() {
        return "PropertyData{" +
                "service='" + service + '\'' +
                ", sagas=" + sagas +
                ", sagaTasks=" + sagaTasks +
                ", jobTasks=" + jobTasks +
                ", timedTasks=" + timedTasks +
                '}';
    }
}
