package io.choerodon.mybatis.common.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jialong.zuo@hand-china.com on 2018/1/10.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinLov {

    String lovCode() default "";

    String dynamicLovColumn() default "";

    String joinKey();
}
