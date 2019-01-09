package io.choerodon.asgard.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

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
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
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

}
