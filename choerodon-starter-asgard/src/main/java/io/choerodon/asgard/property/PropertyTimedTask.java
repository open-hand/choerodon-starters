package io.choerodon.asgard.property;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.asgard.schedule.exception.JobParamDefaultValueParseException;
import io.choerodon.core.exception.CommonException;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PropertyTimedTask {

    private String name;
    private String description;
    private Boolean oneExecution;
    private String methodCode;
    private Map<String, Object> params;
    private Integer repeatCount;
    private Long repeatInterval;
    private String repeatIntervalUnit;
    private String triggerType;
    private String cronExpression;

    PropertyTimedTask(final TimedTask timedTask, final JobTask jobTask) {
        this.name = timedTask.name();
        this.description = timedTask.description();
        this.oneExecution = timedTask.oneExecution();
        this.methodCode = jobTask.code();
        TaskParam[] taskParams = timedTask.params();
        JobParam[] jobParams = jobTask.params();
        this.triggerType = timedTask.triggerType();
        String SIMPLE_TRIGGER = "simple_trigger";
        if (timedTask.triggerType().equals(SIMPLE_TRIGGER)) {
            this.repeatCount = timedTask.repeatCount();
            this.repeatInterval = timedTask.repeatInterval();
            this.repeatIntervalUnit = timedTask.repeatIntervalUnit().name();
        } else {
            this.cronExpression = timedTask.cronExpression();
        }
        //参数转换至map
        this.params = new HashMap<>();
        for (TaskParam taskParam : taskParams) {
            //参数转换至map.1.已赋值参数加入map
            JobParam jobParam = Arrays.stream(jobParams).filter(t -> t.name().equals(taskParam.name())).collect(Collectors.toList()).get(0);
            String key = taskParam.name();
            Object value = getValueByType(jobParam.type(), taskParam.value());
            this.params.put(key, value);
        }
        // 参数转换至map.2.未赋值参数，取默认值，加入map 3.无默认值，抛异常
        List<String> taskName = Arrays.stream(taskParams).map(TaskParam::name).collect(Collectors.toList());
        List<JobParam> collect = Arrays.stream(jobParams).filter(jobParam -> !taskName.contains(jobParam.name())).collect(Collectors.toList());
        for (JobParam jobParam : collect) {
            String key = jobParam.name();
            if (!ValueConstants.DEFAULT_NONE.equals(jobParam.defaultValue())) {
                Object value = getValueByType(jobParam.type(), jobParam.defaultValue());
                this.params.put(key, value);
            } else {
                throw new CommonException("error.timedTask.create.paramsValueEmpty:" + jobParam.name());
            }
        }
    }

    public PropertyTimedTask() {
    }

    private Object getValueByType(Class<?> type, String value) {
        try {
            return type.getConstructor(String.class).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JobParamDefaultValueParseException(e, type, value);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOneExecution() {
        return oneExecution;
    }

    public void setOneExecution(Boolean oneExecution) {
        this.oneExecution = oneExecution;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(Long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getRepeatIntervalUnit() {
        return repeatIntervalUnit;
    }

    public void setRepeatIntervalUnit(String repeatIntervalUnit) {
        this.repeatIntervalUnit = repeatIntervalUnit;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        return "PropertyTimedTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", oneExecution=" + oneExecution +
                ", methodCode='" + methodCode + '\'' +
                ", params=" + params +
                ", repeatCount=" + repeatCount +
                ", repeatInterval=" + repeatInterval +
                ", repeatIntervalUnit='" + repeatIntervalUnit + '\'' +
                '}';
    }
}
