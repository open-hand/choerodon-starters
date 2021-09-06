package io.choerodon.nacos.client.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.nacos.api.PropertyKeyConst;
import io.choerodon.nacos.api.common.Constants;
import io.choerodon.nacos.api.exception.NacosException;
import io.choerodon.nacos.api.user.UserService;
import io.choerodon.nacos.client.config.http.HttpAgent;
import io.choerodon.nacos.client.config.http.MetricsHttpAgent;
import io.choerodon.nacos.client.config.http.ServerHttpAgent;
import io.choerodon.nacos.client.utils.ValidatorUtils;
import io.choerodon.nacos.common.http.HttpRestResult;
import io.choerodon.nacos.common.utils.StringUtils;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NacosUserService implements UserService {
    private static final long POST_TIMEOUT = 3000L;

    /**
     * http agent.
     */
    private final HttpAgent agent;

    private final String encode;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public NacosUserService(Properties properties) throws NacosException {
        ValidatorUtils.checkInitParam(properties);
        String encodeTmp = properties.getProperty(PropertyKeyConst.ENCODE);
        if (StringUtils.isBlank(encodeTmp)) {
            this.encode = Constants.ENCODE;
        } else {
            this.encode = encodeTmp.trim();
        }
        this.agent = new MetricsHttpAgent(new ServerHttpAgent(properties));
        this.agent.start();
    }

    @Override
    public Object createUser(String username, String password) throws NacosException {
        String url = Constants.USER_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("username", username);
        params.put("password", password);
        return doHttpPost(url, null, params, encode, POST_TIMEOUT);
    }

    @Override
    public Object addRole(String role, String username) throws NacosException {
        String url = Constants.ROLE_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("role", role);
        params.put("username", username);
        return doHttpPost(url, null, params, encode, POST_TIMEOUT);
    }

    @Override
    public Object addPermission(String role, String resource, String action) throws NacosException {
        String url = Constants.PERMISSION_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("role", role);
        params.put("resource", resource);
        params.put("action", action);
        return doHttpPost(url, null, params, encode, POST_TIMEOUT);
    }

    private Object doHttpPost(String url, Object o, Map<String, String> params, String encode, long postTimeout) throws NacosException {
        HttpRestResult<String> result = null;
        try {
            result = agent.httpPost(url, null, params, encode, POST_TIMEOUT);
        } catch (Exception ex) {
            return false;
        }
        if (result.ok()) {
            return true;
        } else if (HttpURLConnection.HTTP_FORBIDDEN == result.getCode()) {
            throw new NacosException(result.getCode(), result.getMessage());
        } else {
            return false;
        }
    }


}
