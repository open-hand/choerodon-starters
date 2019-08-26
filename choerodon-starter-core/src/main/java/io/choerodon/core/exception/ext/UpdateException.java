package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 更新异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class UpdateException extends CommonException {
    public UpdateException(String code, Object... parameters) {
        super(code, parameters);
    }

    public UpdateException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public UpdateException(String code, Throwable cause) {
        super(code, cause);
    }

    public UpdateException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
