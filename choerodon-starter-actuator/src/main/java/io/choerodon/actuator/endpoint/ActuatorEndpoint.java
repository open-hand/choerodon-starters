package io.choerodon.actuator.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.swagger.PermissionData;
import io.choerodon.swagger.annotation.Permission;

@RestController
@RequestMapping("/choerodon")
public class ActuatorEndpoint {
    private JsonNode microServiceInitData;
    private Map<String, PermissionData> permissions = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ActuatorEndpoint.class);

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("choerodon/actuator/{key}")
    @Permission(permissionPublic = true, permissionWithin = true)
    private Map<String, Object> query(@PathVariable String key) {
        Map<String, Object> result = new TreeMap<>();
        if ("init-data".equals(key) || "all".equals(key)) {
            if (microServiceInitData == null) {
                try {
                    microServiceInitData = objectMapper.readTree(getClass().getResourceAsStream("/CHOERODON-META/micro-service-init-data.json"));
                } catch (IOException e) {
                    logger.warn("Micro util init data read exception", e);
                }
            }
            if (microServiceInitData == null) {
                microServiceInitData = JsonNodeFactory.instance.objectNode();
            }
            result.put("init-data", microServiceInitData);
        }
        return result;
    }

}
