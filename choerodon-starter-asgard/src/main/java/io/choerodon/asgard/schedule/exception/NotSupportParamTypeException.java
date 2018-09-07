package io.choerodon.asgard.schedule.exception;

import io.choerodon.asgard.schedule.ParamType;

public class NotSupportParamTypeException extends RuntimeException {


    public NotSupportParamTypeException(final Class<?> claz) {
        super("not support parameter type " + claz.getName() + ", only support for " + types(ParamType.values()));
    }

    private static String types(final ParamType[] paramTypes) {
        StringBuilder builder = new StringBuilder();
        for (ParamType paramType : paramTypes) {
            builder.append(paramType.getValue()).append(",");
        }
        return builder.toString();
    }

}
