package io.choerodon.asgard.property;

import java.lang.reflect.InvocationTargetException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ValueConstants;

import io.choerodon.asgard.saga.SagaMonitor;
import io.choerodon.asgard.schedule.ParamType;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.exception.JobParamDefaultValueParseException;
import io.choerodon.asgard.schedule.exception.NotSupportParamTypeException;

@Getter
@Setter
@NoArgsConstructor
public class PropertyJobParam {
    private static final Logger LOGGER = LoggerFactory.getLogger(SagaMonitor.class);

    private String name;

    private Object defaultValue;

    private String type;

    private String description;

    public PropertyJobParam(final JobParam jobParam) {
        this.name = jobParam.name();
        this.type = getParamTypeByClass(jobParam.type()).getValue();
        if (!ValueConstants.DEFAULT_NONE.equals(jobParam.defaultValue())) {
            try {
                this.defaultValue = jobParam.type().getConstructor(String.class).newInstance(jobParam.defaultValue());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new JobParamDefaultValueParseException(e, jobParam.type(), jobParam.defaultValue());
            }

        }
        this.description=jobParam.description();
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

}
