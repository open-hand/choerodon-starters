package io.choerodon.statemachine.annotation;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证器
 *
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface Validator {
    String code();

    String name();

    String description();
}
