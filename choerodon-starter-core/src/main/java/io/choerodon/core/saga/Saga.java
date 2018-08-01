package io.choerodon.core.saga;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author flyleft
 * 2018/4/10
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Saga {

    /**
     * code，唯一
     */
    String code();

    /**
     * 描述
     */
    String description() default "";

    /**
     *输入参数json
     */
    String inputSchema() default "";

}
