package io.choerodon.mybatis.common.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author njq.niu@hand-china.com
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Where {

    Comparison comparison() default Comparison.EQUAL;

    String expression() default "";

    /**
     * don't use this field as where.
     * @return default false
     */
    boolean exclude() default false;
}
