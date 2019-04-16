package io.choerodon.resource.permission;

import io.choerodon.base.annotation.Permission;
import org.springframework.core.annotation.Order;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * @author flyleft
 * 2018/4/16
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1100)
public class PublicPermissionOperationPlugin implements OperationBuilderPlugin {

    private final Set<PublicPermission> publicPermissions = new HashSet<>();

    @Override
    public void apply(OperationContext context) {
        Permission permission = context.findAnnotation(Permission.class).orNull();
        if (permission != null && permission.permissionPublic()) {
            publicPermissions.add(new PublicPermission(context.requestMappingPattern(), context.httpMethod()));
        }

    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

    public Set<PublicPermission> getPublicPaths() {
        return publicPermissions;
    }
}
