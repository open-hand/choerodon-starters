package io.choerodon.asgard.property;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.ParamType;
import io.choerodon.asgard.schedule.exception.NotSupportParamTypeException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropertyJobParam {

    private String name;

    private String defaultValue;

    private String type;

    public PropertyJobParam(final JobParam jobParam) {
        this.name = jobParam.name();
        this.defaultValue = jobParam.defaultValue();
        this.type = getParamTypeByClass(jobParam.type()).getValue();
    }

    private ParamType getParamTypeByClass(final Class<?> claz) {
        if (claz.equals(String.class)) {
            return ParamType.STRING;
        }
        if (claz.equals(Byte.class)) {
            return ParamType.BYTE;
        }
        if (claz.equals(Short.class)) {
            return ParamType.SHORT;
        }
        if (claz.equals(Character.class)) {
            return ParamType.CHARACTER;
        }
        if (claz.equals(Integer.class)) {
            return ParamType.INTEGER;
        }
        if (claz.equals(Long.class)) {
            return ParamType.LONG;
        }
        if (claz.equals(Float.class)) {
            return ParamType.FLOAT;
        }
        if (claz.equals(Double.class)) {
            return ParamType.DOUBLE;
        }
        throw new NotSupportParamTypeException(claz);
    }

}
