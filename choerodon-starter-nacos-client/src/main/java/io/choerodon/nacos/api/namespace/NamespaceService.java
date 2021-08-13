package io.choerodon.nacos.api.namespace;

import io.choerodon.nacos.api.exception.NacosException;
import io.choerodon.nacos.api.namespace.pojo.Namespace;
import io.choerodon.nacos.api.namespace.pojo.NamespaceAllInfo;

import java.io.IOException;
import java.util.List;

public interface NamespaceService {
    /**
     * create namespace
     * @param customNamespaceId
     * @param namespaceName
     * @return
     * @throws NacosException
     */
    boolean createNamespace(String customNamespaceId, String namespaceName) throws NacosException;

    /**
     * create namespace
     * @param customNamespaceId
     * @param namespaceName
     * @return
     * @throws NacosException
     */
    boolean createNamespace(String customNamespaceId, String namespaceName, String namespaceDesc) throws NacosException;

    /**
     * delete namespace
     *
     * @param namespaceId
     * @return
     * @throws NacosException
     */
    boolean deleteNamespace(String namespaceId) throws NacosException;

    /**
     * 查询命名空间
     *
     * @param namespaceId
     * @return
     * @throws NacosException
     */
    NamespaceAllInfo getNamespace(String namespaceId) throws NacosException, IOException;

    /**
     * 查询命名空间列表
     *
     * @return
     * @throws NacosException
     */
    List<Namespace> getNamespaces() throws NacosException, IOException;
}
