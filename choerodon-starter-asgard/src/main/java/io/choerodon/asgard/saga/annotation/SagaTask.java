package io.choerodon.asgard.saga.annotation;

import io.choerodon.asgard.saga.SagaDefinition;

import org.springframework.transaction.annotation.Isolation;

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
     *
     * @return 任务的code
     */
    String code();

    /**
     * 所属的saga的code
     *
     * @return 所属的saga的code
     */
    String sagaCode();

    /**
     * 该任务的在saga中的执行次序。
     * 越小越先执行
     *
     * @return 在saga中的执行次序
     */
    int seq();

    /**
     * 是否在数据库中记录消息消费
     * 记录之后可增强数据一致性, 启用之后
     *
     * @return 是否在数据库中记录消息消费
     */
    boolean enabledDbRecord() default false;

    /**
     * 描述
     *
     * @return 描述
     */
    String description() default "";

    /**
     * 超时时间
     *
     * @return 超时时间
     */
    int timeoutSeconds() default 300;

    /**
     * 最大并发数
     * 并发策略不为null时生效
     *
     * @return 最大并发数
     */
    int concurrentLimitNum() default 1;

    /**
     * 并发策略
     * NONE: 不设置并发限制
     * TYPE: 根据ref_type限制并发
     * TYPE_AND_ID: 根据ref_type和ref_id共同限制并发
     *
     * @return 并发策略
     */
    SagaDefinition.ConcurrentLimitPolicy concurrentLimitPolicy() default SagaDefinition.ConcurrentLimitPolicy.NONE;

    /**
     * 超时策略
     *
     * @return 超时策略
     */
    SagaDefinition.TimeoutPolicy timeoutPolicy() default SagaDefinition.TimeoutPolicy.RETRY;

    /**
     * 最大重试次数。超时策略为SagaDef.TimeoutPolicy.RETRY时生效
     *
     * @return 最大重试次数
     */
    int maxRetryCount() default 1;

    /**
     * 通过类手动指定输出参数。根据类自动生成。
     * outputSchema优先级 大于 优先级 大于 方法返回值优先级
     *
     * @return 通过类手动指定输出参数
     */
    Class<?> outputSchemaClass() default Object.class;

    /**
     * 通过json字符串手动指定输出参数。比如{"name":"wang","age":23}
     * 优先级 大于 outputSchemaClass()优先级 大于 方法返回值优先级。
     * 不为空时会覆盖outputSchemaClass生成的json schema。
     *
     * @return 通过json字符串手动指定输出参数
     */
    String outputSchema() default "";

    /**
     * 事务的隔离级别
     *
     * @return 事务的隔离级别
     */
    Isolation transactionIsolation() default Isolation.DEFAULT;


    /**
     * 所用的事务管理器的bean名
     *
     * @return 所用的事务管理器的bean名
     */
    String transactionManager() default "";

    String failureCallbackMethod() default "";
}
