package io.choerodon.asgard.schedule.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.ValueConstants;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface JobParam {

    String name();

    String defaultValue() default ValueConstants.DEFAULT_NONE;

    Class<?> type() default String.class;

    String description() default "";

}
