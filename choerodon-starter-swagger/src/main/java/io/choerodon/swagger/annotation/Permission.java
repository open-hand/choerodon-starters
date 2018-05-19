package io.choerodon.swagger.annotation;

import io.choerodon.core.iam.ResourceLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限定义注释
 * @author xausky
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * 角色
     * @return 角色数组
     */
    String[] roles() default {"admin"};

    /**
     * 级别
     * @return 级别
     */
    ResourceLevel level() default ResourceLevel.PROJECT;

    /**
     * 登陆后即可拥有的权限
     * @return 是否拥有此权限
     */
    boolean permissionLogin() default false;

    /**
     * 公共权限
     * @return 是否拥有此权限
     */
    boolean permissionPublic() default false;
}
