/*
 * #{copyright}#
 */

package io.choerodon.core.annotation;

import java.lang.annotation.*;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Inherited
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSet {
    String cache();
}
