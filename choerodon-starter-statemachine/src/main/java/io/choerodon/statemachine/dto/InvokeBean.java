package io.choerodon.statemachine.dto;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Method;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class InvokeBean {

    private Method method;

    private Object object;

    private String code;

    public InvokeBean(Method method, Object object, String code) {
        this.method = method;
        this.object = object;
        this.code = code;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("method", method)
                .add("object", object)
                .add("code", code)
                .toString();
    }
}
