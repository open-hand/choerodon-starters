package io.choerodon.nacos.api.namespace;

import io.choerodon.nacos.api.exception.NacosException;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class NamespaceFactory {
    /**
     * Create namespace.
     *
     * @param properties init param
     * @return ConfigService
     * @throws NacosException Exception
     */
    public static NamespaceService createNamespaceService(Properties properties) throws NacosException {
        try {
            Class<?> driverImplClass = Class.forName("io.choerodon.nacos.client.namespace.NacosNamespaceService");
            Constructor constructor = driverImplClass.getConstructor(Properties.class);
            NamespaceService vendorImpl = (NamespaceService) constructor.newInstance(properties);
            return vendorImpl;
        } catch (Throwable e) {
            throw new NacosException(NacosException.CLIENT_INVALID_PARAM, e);
        }
    }
}
