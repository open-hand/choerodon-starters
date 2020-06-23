package io.choerodon.core.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author scp
 * @date 2020/5/29
 * @description
 */

public class CommonException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(CommonException.class);
    private static final long serialVersionUID = 5044938065901970022L;
    private final transient Object[] parameters;
    private String code;

    public CommonException(String code, Object... parameters) {
        super(code);
        this.parameters = parameters;
        this.code = code;
    }

    public CommonException(String code, Throwable cause, Object... parameters) {
        super(code, cause);
        this.parameters = parameters;
        this.code = code;
    }

    public CommonException(String code, Throwable cause) {
        super(code, cause);
        this.code = code;
        this.parameters = new Object[0];
    }

    public CommonException(Throwable cause, Object... parameters) {
        super((String)null, cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public String getCode() {
        return this.code;
    }

    public String getTrace() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PrintStream ps;
        try {
            ps = new PrintStream(baos, false, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException var4) {
            logger.error("Error get trace, unsupported encoding.", var4);
            return null;
        }

        this.printStackTrace(ps);
        ps.flush();
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new LinkedHashMap();
        map.put("code", this.code);
        map.put("message", super.getMessage());
        return map;
    }
}
