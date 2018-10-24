package io.choerodon.asgard.property;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.ValueConstants;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.TaskParam;
import io.choerodon.asgard.schedule.exception.JobParamDefaultValueParseException;
import io.choerodon.core.exception.CommonException;

@Getter
@Setter
@NoArgsConstructor
public class PropertyTimedTask {

    private String name;
    private String description;
    private Boolean oneExecution;
    private String methodCode;
    private Map<String, Object> params;
    private Integer repeatCount;
    private Long repeatInterval;
    private String repeatIntervalUnit;

    public PropertyTimedTask(String name, String description, Boolean oneExecution,
                             String methodCode, TaskParam[] params, JobParam[] jobParams,
                             Integer repeatCount, Long repeatInterval, String repeatIntervalUnit) {
        this.name = name;
        this.description = description;
        this.oneExecution = oneExecution;
        this.methodCode = methodCode;
        this.repeatCount = repeatCount;
        this.repeatInterval = repeatInterval;
        this.repeatIntervalUnit = repeatIntervalUnit;
        //参数转换至map
        this.params = new HashMap<>();
        for (TaskParam taskParam : params) {
            //参数转换至map.1.已赋值参数加入map
            JobParam jobParam = Arrays.stream(jobParams).filter(t -> t.name().equals(taskParam.name())).collect(Collectors.toList()).get(0);
            String key = taskParam.name();
            Object value = getValueByType(jobParam.type(), taskParam.value());
            this.params.put(key, value);
        }
        // 参数转换至map.2.未赋值参数，取默认值，加入map 3.无默认值，抛异常
        List<String> taskName = Arrays.stream(params).map(t -> t.name()).collect(Collectors.toList());
        List<JobParam> collect = Arrays.stream(jobParams).filter(jobParam -> !taskName.contains(jobParam.name())).collect(Collectors.toList());
        for (JobParam jobParam : collect) {
            String key = jobParam.name();
            if (!ValueConstants.DEFAULT_NONE.equals(jobParam.defaultValue())) {
                Object value = getValueByType(jobParam.type(), jobParam.defaultValue());
                this.params.put(key, value);
            } else {
                throw new CommonException("error.create.timedtask.params.value.empty:" + jobParam.name());
            }
        }
    }

    private Object getValueByType(Class<?> type, String value) {
        try {
            return type.getConstructor(String.class).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new JobParamDefaultValueParseException(e, type, value);
        }
    }
}
