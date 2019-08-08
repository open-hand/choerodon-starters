package io.choerodon.resource.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author superlee
 * @since 2019-08-08
 */
public class AuthenticationRequestFailedException extends AuthenticationException {
    public AuthenticationRequestFailedException(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationRequestFailedException(String msg) {
        super(msg);
    }
}
