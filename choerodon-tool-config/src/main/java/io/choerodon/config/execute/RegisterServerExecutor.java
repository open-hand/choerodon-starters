package io.choerodon.config.execute;

import io.choerodon.config.utils.CreateConfigDTO;
import io.choerodon.config.utils.InitConfigException;
import io.choerodon.config.utils.InitConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static io.choerodon.config.utils.InitConfigProperties.*;

public class RegisterServerExecutor extends AbstractExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterServerExecutor.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void execute(InitConfigProperties properties, String configFile) throws IOException {
        String updatePolicy = properties.getConfig().getUpdatePolicy();
        switch (updatePolicy) {
            case UPDATE_POLICY_ADD:
            case UPDATE_POLICY_NOT:
            case UPDATE_POLICY_OVERRIDE:
            case UPDATE_POLICY_UPDATE:
                postConfigToRegisterServer(properties, configFile, updatePolicy);
                break;
            default:
                throw new InitConfigException("invalid update policy, update policy is only one of 'not'、'add'、'override' and 'update'");
        }
    }

    private void postConfigToRegisterServer(InitConfigProperties properties, String configFile, String updatePolicy) throws IOException {
        CreateConfigDTO dto = new CreateConfigDTO().setService(properties.getService().getName())
                .setVersion(properties.getService().getVersion())
                .setNamespace(properties.getService().getNamespace())
                .setProfile(properties.getConfig().getProfile())
                .setYaml(readFile(configFile))
                .setUpdatePolicy(updatePolicy);
        ResponseEntity<Void> response = restTemplate.postForEntity(getCreateConfigMapUrl(properties.getRegister().getHost()), dto, Void.class);
        if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
            LOGGER.warn("该服务配置的configMap已存在！");
        } else if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InitConfigException("Create configMap error, statusCode: " + response.getStatusCodeValue());
        }
        LOGGER.info("配置初始化完成");
    }

    private String getCreateConfigMapUrl(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url + "configs";
    }
}
