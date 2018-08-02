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
     * 手动输入参数json
     * 不为empty时会覆盖inputSchemaClass自动生成的json
     */
    String inputSchema() default "";

    /**
     * 根据设置的Class自动生成输入json。
     *
     * 加入传入的class为Saga.class:
     *  public class Saga extends AuditDomain {
     *     private Long id;
     *     private String code;
     *     private String description;
     *     private String inputSchema;
     *     private String service;
     *     //getter setter
     *  }
     *
     * 则生成的json为:
     * {"creationDate":1533195966280,"createdBy":0,"lastUpdateDate":1533195966285,
     * "lastUpdatedBy":0,"objectVersionNumber":0,"id":0,"code":"string","description":"string",
     * "inputSchema":"string","service":"string"}
     */
    Class<?> inputSchemaClass() default Object.class;

}
