package io.choerodon.asgard.schedule.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务自定义定时任务的方法参数
 * @author Eugen
 * 2018/10/22 22:31
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskParam {

    String name();

    String value();

}
