package io.choerodon.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加在 DTO 的某个 field 上,表示这个 field 是用存放子节点信息.
 * <p>
 * 头行结构中行的标记,也可以用作单个对象的子属性标记
 * <p>
 * 起作用的地方
 * <ul>
 * <li>自动校验(可以递归校验子节点)</li>
 * <li>自动设置BaseDTO 的StdWho等信息(自动蔓延)</li>
 * </ul>
 *
 * @author shengyang.zhou@hand-china.com
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Children {
}