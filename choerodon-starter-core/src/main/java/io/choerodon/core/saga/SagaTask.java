package io.choerodon.core.saga;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author flyleft
 * 2018/4/10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SagaTask {

    /**
     * 任务的code，唯一
     */
    String code();

    /**
     * 描述
     */
    String description() default "";


    /**
     * 所属的saga的code
     */
    String sagaCode();

    /**
     * 该任务的在saga中的执行次序。
     * 越小越先执行
     */
    int seq();

    /**
     * 该任务执行的最大并发数
     */
    int concurrentExecLimit() default 1;

    /**
     * 该任务自动重试的最大重试次数
     */
    int maxRetryCount() default 0;


}
