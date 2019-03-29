package io.choerodon.mybatis.common.query;


import javax.persistence.criteria.JoinType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author njq.niu@hand-china.com
 */
@Repeatable(JoinTables.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {

    String name();

    Class<?> target();


    JoinType type() default JoinType.INNER;

    JoinOn[] on() default {};

    boolean joinMultiLanguageTable() default false;
}
