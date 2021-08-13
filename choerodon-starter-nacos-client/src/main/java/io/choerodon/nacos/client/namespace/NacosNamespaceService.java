package io.choerodon.nacos.client.namespace;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.nacos.api.PropertyKeyConst;
import io.choerodon.nacos.api.common.Constants;
import io.choerodon.nacos.api.exception.NacosException;
import io.choerodon.nacos.api.namespace.NamespaceService;
import io.choerodon.nacos.api.namespace.pojo.Namespace;
import io.choerodon.nacos.api.namespace.pojo.NamespaceAllInfo;
import io.choerodon.nacos.client.config.http.HttpAgent;
import io.choerodon.nacos.client.config.http.MetricsHttpAgent;
import io.choerodon.nacos.client.config.http.ServerHttpAgent;
import io.choerodon.nacos.client.utils.LogUtils;
import io.choerodon.nacos.client.utils.ValidatorUtils;
import io.choerodon.nacos.common.http.HttpRestResult;
import io.choerodon.nacos.common.utils.StringUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NacosNamespaceService implements NamespaceService {
    private static final Logger LOGGER = LogUtils.logger(NacosNamespaceService.class);

    private static final long POST_TIMEOUT = 3000L;

    /**
     * http agent.
     */
    private final HttpAgent agent;

    private final String encode;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public NacosNamespaceService(Properties properties) throws NacosException {
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
    public boolean createNamespace(String customNamespaceId, String namespaceName) throws NacosException {
        return createNamespaceInner(customNamespaceId, namespaceName, null);
    }

    @Override
    public boolean createNamespace(String customNamespaceId, String namespaceName, String namespaceDesc) throws NacosException {
        return createNamespaceInner(customNamespaceId, namespaceName, namespaceDesc);
    }

    @Override
    public boolean deleteNamespace(String namespaceId) throws NacosException {
        return deleteNamespaceInner(namespaceId);
    }

    @Override
    public NamespaceAllInfo getNamespace(String namespaceId) throws NacosException, IOException {
        return getNamespaceInner(namespaceId);
    }

    @Override
    public List<Namespace> getNamespaces() throws NacosException, IOException {
        return getNamespacesInner();
    }

    private boolean createNamespaceInner(String customNamespaceId, String namespaceName, String namespaceDesc) throws NacosException {
        String url = Constants.NAMESPACE_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("customNamespaceId", customNamespaceId);
        params.put("namespaceName", namespaceName);
        params.put("namespaceDesc", namespaceDesc);
        if (StringUtils.isNotEmpty(namespaceDesc)) {
            params.put("namespaceDesc", namespaceDesc);
        }
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

    private boolean deleteNamespaceInner(String namespaceId) throws NacosException {
        String url = Constants.NAMESPACE_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("namespaceId", namespaceId);
        HttpRestResult<String> result = null;
        try {
            result = agent.httpDelete(url, null, params, encode, POST_TIMEOUT);
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

    private NamespaceAllInfo getNamespaceInner(String namespaceId) throws NacosException, IOException {
        String url = Constants.NAMESPACE_CONTROLLER_PATH;
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("show", "all");
        params.put("namespaceId", namespaceId);
        HttpRestResult<String> result = null;
        try {
            result = agent.httpGet(url, null, params, encode, POST_TIMEOUT);
        } catch (Exception ex) {
            return null;
        }
        if (result.ok()) {
            String data = result.getData();
            return objectMapper.readValue(data, NamespaceAllInfo.class);
        } else if (HttpURLConnection.HTTP_FORBIDDEN == result.getCode()) {
            throw new NacosException(result.getCode(), result.getMessage());
        } else {
            return null;
        }
    }

    private List<Namespace> getNamespacesInner() throws NacosException, IOException {
        String url = Constants.NAMESPACE_CONTROLLER_PATH;
        HttpRestResult<String> result = null;
        try {
            result = agent.httpGet(url, null, null, encode, POST_TIMEOUT);
        } catch (Exception ex) {
            return null;
        }
        if (result.ok()) {
            String data = result.getData();
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                    Namespace.class);
            return objectMapper.readValue(data, javaType);
        } else if (HttpURLConnection.HTTP_FORBIDDEN == result.getCode()) {
            throw new NacosException(result.getCode(), result.getMessage());
        } else {
            return null;
        }
    }
}
