package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 对象已存在异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class AlreadyExsitedException extends CommonException {
    public AlreadyExsitedException(String code, Object... parameters) {
        super(code, parameters);
    }

    public AlreadyExsitedException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public AlreadyExsitedException(String code, Throwable cause) {
        super(code, cause);
    }

    public AlreadyExsitedException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
