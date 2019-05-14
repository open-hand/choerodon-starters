package io.choerodon.annotation;

import io.choerodon.annotation.entity.PermissionDescription;
import io.choerodon.annotation.entity.PermissionEntity;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Map;

public class PermissionProcessor {

    private PermissionProcessor(){
    }

    public static void resolve(Map<String, Object> controllers, Map<String, PermissionDescription> descriptions){
        for (Object controller: controllers.values()){
            resolveClass(controller.getClass(), descriptions);
        }
    }

    public static void resolveClass(Class clazz, Map<String, PermissionDescription> descriptions){
        Controller controller = AnnotationUtils.findAnnotation(clazz, Controller.class);
        if (controller != null){
            RequestMapping controllerMapping = AnnotatedElementUtils.getMergedAnnotation(clazz, RequestMapping.class);
            String controllerPath = "";
            if (controllerMapping != null && controllerMapping.value().length > 0){
                controllerPath = controllerMapping.value()[0];
            }
            for (Method method : clazz.getMethods()) {
                resolveMethod(clazz, method, controllerPath, descriptions);
            }
        }
        Dataset dataset = AnnotationUtils.findAnnotation(clazz, Dataset.class);
        if (dataset != null){
            resolveDataset(descriptions, dataset.value());
        }
    }

    private static void resolveDataset(Map<String, PermissionDescription> descriptions, String name){
        String[] datasetActions = new String[]{"queries", "mutations", "languages", "validate", "export"};
        for (String action : datasetActions){
            PermissionDescription description = new PermissionDescription();
            PermissionEntity permissionEntity = new PermissionEntity();
            permissionEntity.setType(ResourceType.SITE.value());
            permissionEntity.setPermissionLogin(false);
            permissionEntity.setPermissionWithin(false);
            permissionEntity.setPermissionPublic(false);

            description.setPath(String.format("/dataset/%s/%s", name, action));
            description.setPermission(permissionEntity);
            description.setMethod("post");
            descriptions.put(String.format("io.cherodon.dataset.%sDatasetController.%s", name, action), description);
        }
    }

    private static void resolveMethod(Class clazz, Method method, String controllerPath, Map<String, PermissionDescription> descriptions){
        RequestMapping methodMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        if (methodMapping != null){
            String methodPath = "";
            if (methodMapping.value().length > 0){
                methodPath = methodMapping.value()[0];
            }
            PermissionDescription description = new PermissionDescription();
            description.setPath(processPath(controllerPath + methodPath));
            RequestMethod requestMethod = RequestMethod.GET;
            if (methodMapping.method().length > 0){
                requestMethod = methodMapping.method()[0];
            }
            description.setMethod(requestMethod.name().toLowerCase());
            Permission permission = AnnotationUtils.getAnnotation(method, Permission.class);
            if (permission != null) {
                PermissionEntity permissionEntity = new PermissionEntity();
                permissionEntity.setRoles(permission.roles());
                permissionEntity.setType(permission.type().value());
                permissionEntity.setPermissionLogin(permission.permissionLogin());
                permissionEntity.setPermissionPublic(permission.permissionPublic());
                permissionEntity.setPermissionWithin(permission.permissionWithin());
                description.setPermission(permissionEntity);
            }
            descriptions.put(clazz.getName() + "." + method.getName(), description);
        }
    }

    private static String processPath(String path){
        if (!path.startsWith("/")){
            path = "/" + path;
        }
        return path.replace("//", "/");
    }
}
