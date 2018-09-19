package io.choerodon.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import io.choerodon.core.swagger.LabelData;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.swagger.annotation.Label;
import io.choerodon.swagger.annotation.Permission;

/**
 * 解析接口的@Permission注解，将权限信息将乳到swagger的描述节点
 *
 * @author xausky
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class CustomSwaggerOperationPlugin implements OperationBuilderPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSwaggerOperationPlugin.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void apply(OperationContext context) {
        SwaggerExtraData swaggerExtraData = new SwaggerExtraData();
        Permission permission = context.findAnnotation(Permission.class).orNull();
        if (permission != null) {
            PermissionData permissionData = new PermissionData();
            permissionData.setAction(context.getName());
            permissionData.setPermissionLevel(permission.level().value());
            permissionData.setPermissionLogin(permission.permissionLogin());
            permissionData.setPermissionPublic(permission.permissionPublic());
            permissionData.setRoles(permission.roles());
            permissionData.setPermissionWithin(permission.permissionWithin());
            swaggerExtraData.setPermission(permissionData);
        }
        Label label = context.findAnnotation(Label.class).orNull();
        if (label != null) {
            LabelData labelData = new LabelData();
            labelData.setRoleName(label.roleName());
            labelData.setType(label.type());
            labelData.setLevel(label.level());
            labelData.setLabelName(label.labelName());
            swaggerExtraData.setLabel(labelData);
        }
        if (swaggerExtraData.getPermission() != null || swaggerExtraData.getLabel() != null) {
            try {
                context.operationBuilder().notes(mapper.writeValueAsString(swaggerExtraData));
            } catch (JsonProcessingException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}
