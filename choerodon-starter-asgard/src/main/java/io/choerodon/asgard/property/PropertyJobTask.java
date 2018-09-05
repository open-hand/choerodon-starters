package io.choerodon.asgard.property;

import io.choerodon.asgard.quartz.annotation.JobParam;

import java.util.ArrayList;
import java.util.List;

public class PropertyJobTask {

    private String method;

    private int maxRetryCount;

    private List<PropertyJobParam> params;

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public List<PropertyJobParam> getParams() {
        return params;
    }

    public void setParams(List<PropertyJobParam> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    PropertyJobTask(String method, int maxRetryCount, JobParam[] params) {
        this.method = method;
        this.maxRetryCount = maxRetryCount;
        this.params = new ArrayList<>();
        for (JobParam jobParam : params) {
            this.params.add(new PropertyJobParam(jobParam));
        }
    }

    public PropertyJobTask() {
    }
}
