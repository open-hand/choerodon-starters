package io.choerodon.core.utils;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.hzero.starter.keyencrypt.core.EncryptContext;

/**
 * HZERO环境线程核心上下文拷贝装饰器
 * @author gaokuo.dai@zknow.com 2022-08-16
 */
public class HZeroContextCopyDecorator implements TaskDecorator {

    private static final HZeroContextCopyDecorator instance = new HZeroContextCopyDecorator();
    private HZeroContextCopyDecorator(){}

    public static HZeroContextCopyDecorator getInstance() {
        return instance;
    }

    @Override
    public Runnable decorate(@Nonnull Runnable runnable) {
        if (RequestContextHolder.getRequestAttributes() != null) {
            RequestAttributes context = RequestContextHolder.currentRequestAttributes();
            SecurityContext securityContext = SecurityContextHolder.getContext();
            String currentEncrypt = EncryptContext.encryptType().name();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(context);
                    SecurityContextHolder.setContext(securityContext);
                    HttpServletRequest servletRequest = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
                    String encrypt = (String) Optional.ofNullable(servletRequest.getHeader("H-Request-Encrypt")).orElse(currentEncrypt);
                    if (EncryptContext.isAllowedEncrypt() && StringUtils.isNotEmpty(encrypt)) {
                        EncryptContext.setEncryptType(encrypt);
                    }
                    runnable.run();
                } finally {
                    SecurityContextHolder.clearContext();
                    RequestContextHolder.resetRequestAttributes();
                }

            };
        } else {
            return runnable;
        }
    }
}
