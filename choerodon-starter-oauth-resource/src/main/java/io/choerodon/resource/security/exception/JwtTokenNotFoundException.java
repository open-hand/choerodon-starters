package io.choerodon.resource.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author superlee
 * @since 2019-08-08
 */
public class JwtTokenNotFoundException extends AuthenticationException {
    public JwtTokenNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public JwtTokenNotFoundException(String msg) {
        super(msg);
    }
}
