package io.choerodon.asgard.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.hzero.core.message.Message;
import org.hzero.core.message.MessageAccessor;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

public class InstanceResultUtils {

    private InstanceResultUtils() {
    }

    public static String resultToJson(final Object result, final ObjectMapper objectMapper) throws IOException {
        if (result == null) {
            return null;
        }
        if (result instanceof String) {
            String resultStr = (String) result;
            if (resultStr.isEmpty()) {
                return null;
            }
            JsonNode jsonNode = objectMapper.readTree(resultStr);
            if (!(jsonNode instanceof ValueNode)) {
                return resultStr;
            }
        }
        return objectMapper.writeValueAsString(result);
    }

    public static String getErrorInfoFromException(Throwable e) {
        try {
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException) e).getTargetException();
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            processCommonException(e, pw, String.format("Exception in thread \"%s\" ", Thread.currentThread().getName()));
            return "\r\n" + sw + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

    public static Throwable getLoggerException(Exception e) {
        if (e instanceof InvocationTargetException) {
            return ((InvocationTargetException) e).getTargetException();
        }
        return e;
    }

    private static void processCommonException(Throwable e, PrintWriter pw, String prefix) {
        if (e == null) {
            return;
        }
        Message message;
        if (e instanceof CommonException) {
            message = MessageAccessor.getMessage(((CommonException) e).getCode(), ((CommonException) e).getParameters());
            if (message == null || StringUtils.isEmpty(message.getDesc())) {
                e.printStackTrace(pw);
            } else {
                pw.println(prefix + e.getClass().getTypeName() + ":" + message.getDesc());
                StackTraceElement[] trace = e.getStackTrace();
                for (StackTraceElement traceElement : trace) {
                    pw.println("\tat " + traceElement);
                }
                processCommonException(e.getCause(), pw, "Caused by: ");

                Arrays.stream(e.getSuppressed()).forEach(t -> processCommonException(t, pw, "\tSuppressed:"));
            }
        } else {
            e.printStackTrace(pw);
        }
    }
}
