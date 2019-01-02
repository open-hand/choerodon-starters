package io.choerodon.asgard.property;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.core.iam.ResourceLevel;

import java.util.ArrayList;
import java.util.List;

public class PropertyJobTask {

    private String method;

    private int maxRetryCount;

    private String code;

    private String description;

    private String level;

    private List<PropertyJobParam> params;

    public PropertyJobTask(String method, int maxRetryCount, String code, String description, ResourceLevel level, JobParam[] params) {
        this.method = method;
        this.maxRetryCount = maxRetryCount;
        this.code = code;
        this.description = description;
        this.level = level.value();
        this.params = new ArrayList<>(params.length);
        for (JobParam jobParam : params) {
            this.params.add(new PropertyJobParam(jobParam));
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<PropertyJobParam> getParams() {
        return params;
    }

    public void setParams(List<PropertyJobParam> params) {
        this.params = params;
    }
}
