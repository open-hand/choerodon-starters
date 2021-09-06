package io.choerodon.nacos.api.user;

import io.choerodon.nacos.api.exception.NacosException;
import io.choerodon.nacos.api.namespace.NamespaceService;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class UserFactory {

    public static UserService createUserService(Properties properties) throws NacosException {
        try {
            Class<?> driverImplClass = Class.forName("io.choerodon.nacos.client.user.NacosUserService");
            Constructor constructor = driverImplClass.getConstructor(Properties.class);
            UserService vendorImpl = (UserService) constructor.newInstance(properties);
            return vendorImpl;
        } catch (Throwable e) {
            throw new NacosException(NacosException.CLIENT_INVALID_PARAM, e);
        }
    }
}
