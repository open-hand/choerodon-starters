package io.choerodon.actuator.endpoint;

import io.choerodon.annotation.PermissionProcessor;
import io.choerodon.annotation.entity.PermissionDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/v2/choerodon/actuator")
public class ActuatorEndpoint {
    private Map<String, PermissionDescription> permissions = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/{key}")
    private Map<String, Object> query(@PathVariable String key){
        Map<String, Object> result = new TreeMap<>();
        if ("permission".equals(key) || "all".equals(key)){
            if (permissions.isEmpty()){
                PermissionProcessor.resolve(applicationContext.getBeansWithAnnotation(Controller.class), permissions);
            }
            result.put("permission", permissions);
        }
        return result;
    }
}
