package io.choerodon.web.validator;

import org.springframework.validation.FieldError;

/**
 * 扩展 FieldError.
 * <p>
 * 直接附加field 所属的 bean
 *
 * @author shengyang.zhou@hand-china.com
 */
public class FieldErrorWithBean extends FieldError {
    private Object targetBean;

    public FieldErrorWithBean(String objectName, String field, Object rejectedValue, boolean bindingFailure,
                              String[] codes, Object[] arguments, String defaultMessage) {
        super(objectName, field, rejectedValue, bindingFailure, codes, arguments, defaultMessage);
    }

    public Object getTargetBean() {
        return targetBean;
    }

    public void setTargetBean(Object targetBean) {
        this.targetBean = targetBean;
    }
}
