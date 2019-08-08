package io.choerodon.resource.annoation;

import java.lang.annotation.*;

import io.choerodon.resource.security.JwtResourceServerConfig;
import org.springframework.context.annotation.Import;

/**
 * @author dongfan117@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JwtResourceServerConfig.class})
public @interface EnableChoerodonResourceServer {
}
