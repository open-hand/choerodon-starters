package io.choerodon.core.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeignException extends RuntimeException {

    private final transient Object[] parameters;

    private String code;

    /**
     * 构造器
     *
     * @param code       异常code
     * @param parameters parameters
     */
    public FeignException(String code, Object... parameters) {
        super(code);
        this.parameters = parameters;
        this.code = code;
    }

    public FeignException(String code, String message) {
        super(message);
        this.code = code;
        this.parameters = new Object[]{};
    }

    public FeignException(String code, Throwable cause, Object... parameters) {
        super(code, cause);
        this.parameters = parameters;
        this.code = code;
    }

    public FeignException(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.parameters = new Object[]{};
    }


    public FeignException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getCode() {
        return code;
    }

    public String getTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        this.printStackTrace(ps);
        ps.flush();
        return new String(baos.toByteArray());
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        map.put("message", super.getMessage());
        return map;
    }

}
