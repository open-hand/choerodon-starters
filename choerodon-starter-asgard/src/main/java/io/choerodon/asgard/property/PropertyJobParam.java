package io.choerodon.asgard.property;

import io.choerodon.asgard.schedule.ParamType;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.exception.JobParamDefaultValueParseException;
import io.choerodon.asgard.schedule.exception.NotSupportParamTypeException;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.InvocationTargetException;

public class PropertyJobParam {

    private String name;

    private Object defaultValue;

    private String type;

    private String description;

    PropertyJobParam(final JobParam jobParam) {
        this.name = jobParam.name();
        this.type = getParamTypeByClass(jobParam.type()).getValue();
        if (!ValueConstants.DEFAULT_NONE.equals(jobParam.defaultValue())) {
            try {
                this.defaultValue = jobParam.type().getConstructor(String.class).newInstance(jobParam.defaultValue());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new JobParamDefaultValueParseException(e, jobParam.type(), jobParam.defaultValue());
            }

        }
        this.description = jobParam.description();
    }

    private ParamType getParamTypeByClass(final Class<?> claz) {
        if (claz.equals(String.class)) {
            return ParamType.STRING;
        }
        if (claz.equals(Integer.class)) {
            return ParamType.INTEGER;
        }
        if (claz.equals(Long.class)) {
            return ParamType.LONG;
        }
        if (claz.equals(Double.class)) {
            return ParamType.DOUBLE;
        }
        if (claz.equals(Boolean.class)) {
            return ParamType.BOOLEAN;
        }
        throw new NotSupportParamTypeException(claz);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
