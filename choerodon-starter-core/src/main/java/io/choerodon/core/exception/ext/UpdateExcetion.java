package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 更新异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class UpdateExcetion extends CommonException {
    public UpdateExcetion(String code, Object... parameters) {
        super(code, parameters);
    }

    public UpdateExcetion(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public UpdateExcetion(String code, Throwable cause) {
        super(code, cause);
    }

    public UpdateExcetion(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
