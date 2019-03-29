package io.choerodon.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jialong.zuo@hand-china.com on 2017/11/15.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelJoinColumn {

    Class JoinTable();

    String JoinColumn() default "";

    String AlternateColumn() default "";

}
