/*
 * #{copyright}#
 */

package io.choerodon.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多语言特性注解.
 * 
 * @author shengyang.zhou@hand-china.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLanguage {
}