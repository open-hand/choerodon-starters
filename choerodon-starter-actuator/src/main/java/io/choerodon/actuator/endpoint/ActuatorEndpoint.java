package io.choerodon.actuator.endpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.choerodon.actuator.metadata.IMetadataDriver;
import io.choerodon.actuator.metadata.dto.MetadataDatabase;
import io.choerodon.annotation.PermissionProcessor;
import io.choerodon.annotation.entity.PermissionDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/choerodon/actuator")
public class ActuatorEndpoint {
    private JsonNode microServiceInitData;
    private MetadataDatabase metadata = null;
    private Map<String, PermissionDescription> permissions = new HashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    private Logger logger = LoggerFactory.getLogger(ActuatorEndpoint.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${spring.datasource.url:null}")
    private String datasourceUrl;

    @Value("${choerodon.tenant-column:organizationId}")
    private String tenantColumn;

    @Autowired
    private IMetadataDriver metadataDriver;

    @GetMapping("/{key}")
    private Map<String, Object> query(@PathVariable String key) {
        Map<String, Object> result = new TreeMap<>();
        if ("permission".equals(key) || "all".equals(key)){
            if (permissions.isEmpty()){
                PermissionProcessor.resolve(applicationContext.getBeansWithAnnotation(Controller.class), permissions);
            }
            result.put("permission", permissions);
        }
        if ("init-data".equals(key) || "all".equals(key)){
            if (microServiceInitData == null){
                try {
                    microServiceInitData = objectMapper.readTree(getClass().getResourceAsStream("/CHOERODON-META/micro-service-init-data.json"));
                } catch (IOException e) {
                    logger.warn("Micro util init data read exception", e);
                }
            }
            if (microServiceInitData == null){
                microServiceInitData = JsonNodeFactory.instance.objectNode();
            }
            result.put("init-data", microServiceInitData);
        }
        if ("metadata".equals(key) || "all".equals(key)){
            if (metadata == null){
                try {
                    metadata = new MetadataDatabase();
                    metadata.setType(datasourceUrl.split(":")[1]);
                    metadata.setTenantColumn(tenantColumn);
                    metadata.setTables(metadataDriver.selectTables());
                } catch (SQLException e) {
                    logger.warn("Read metadata exception", e);
                }
            }
            result.put("metadata", metadata);
        }
        return result;
    }
}
