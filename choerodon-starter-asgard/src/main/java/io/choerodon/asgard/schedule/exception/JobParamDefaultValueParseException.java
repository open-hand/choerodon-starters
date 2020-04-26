package io.choerodon.asgard.schedule.exception;

public class JobParamDefaultValueParseException extends RuntimeException {

    public JobParamDefaultValueParseException(final Exception e, final Class<?> type, final Object value) {
        super("JobParam defaultValue : " + value + " type into Class: " + type + "failed, error:" + e);
    }

}
