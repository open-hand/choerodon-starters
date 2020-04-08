package io.choerodon.plugin.maven;

import java.lang.reflect.Method;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.swagger.annotation.Permission;


public class PermissionProcessor {

    private PermissionProcessor() {
    }

    public static void resolve(Map<String, Object> controllers, Map<String, PermissionData> descriptions) {
        for (Object controller : controllers.values()) {
            resolveClass(controller.getClass(), descriptions);
        }
    }

    public static void resolveClass(Class clazz, Map<String, PermissionData> descriptions) {
        Controller controller = AnnotationUtils.findAnnotation(clazz, Controller.class);
        if (controller != null) {
            RequestMapping controllerMapping = AnnotatedElementUtils.getMergedAnnotation(clazz, RequestMapping.class);
            String controllerPath = "";
            if (controllerMapping != null && controllerMapping.value().length > 0) {
                controllerPath = controllerMapping.value()[0];
            }
            for (Method method : clazz.getDeclaredMethods()) {
                resolveMethod(clazz, method, controllerPath, descriptions);
            }
        }
        // todo  不用dataset 使用sql或mapper
//        Dataset dataset = AnnotationUtils.findAnnotation(clazz, Dataset.class);
//        if (dataset != null) {
//            resolveDataset(descriptions, dataset.value());
//        }
    }

    private static void resolveDataset(Map<String, PermissionData> descriptions, String name) {
        String[] datasetActions = new String[]{"queries", "mutations", "languages", "validate", "export"};
        for (String action : datasetActions) {
            PermissionData description = new PermissionData();
            description.setPermissionLevel(ResourceLevel.SITE.value());
            description.setPermissionLogin(false);
            description.setPermissionWithin(false);
            description.setPermissionPublic(false);
            description.setPath(String.format("/dataset/%s/%s", name, action));
            description.setMethod("post");
            descriptions.put(String.format("io.cherodon.dataset.%sDatasetController.%s", name, action), description);
        }
    }

    private static void resolveMethod(Class clazz, Method method, String controllerPath, Map<String, PermissionData> descriptions) {
        RequestMapping methodMapping = AnnotatedElementUtils.getMergedAnnotation(method, RequestMapping.class);
        if (methodMapping != null) {
            String methodPath = "";
            if (methodMapping.value().length > 0) {
                methodPath = methodMapping.value()[0];
            }
            PermissionData description = new PermissionData();
            description.setPath(processPath(controllerPath + methodPath));
            RequestMethod requestMethod = RequestMethod.GET;
            if (methodMapping.method().length > 0) {
                requestMethod = methodMapping.method()[0];
            }
            description.setMethod(requestMethod.name().toLowerCase());
            Permission permission = AnnotationUtils.getAnnotation(method, Permission.class);
            ApiOperation operation = AnnotationUtils.getAnnotation(method, ApiOperation.class);
            if (permission != null) {
                description.setRoles(permission.roles());
                description.setPermissionLevel(permission.level().value());
                description.setPermissionLogin(permission.permissionLogin());
                description.setPermissionPublic(permission.permissionPublic());
                description.setPermissionWithin(permission.permissionWithin());
                if (operation != null) {
                    description.setDescription(operation.value());
                }
            }
            descriptions.put(clazz.getName() + "." + method.getName(), description);
        }
    }

    private static String processPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path.replace("//", "/");
    }
}
