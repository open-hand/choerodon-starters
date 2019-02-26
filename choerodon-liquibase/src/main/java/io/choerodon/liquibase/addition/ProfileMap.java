package io.choerodon.liquibase.addition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 读取环境变量
 *
 * @author dongfan117@gmail.com
 */
public class ProfileMap implements EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(ProfileMap.class);

    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    /**
     * 获取环境变量.
     *
     * @param key key
     * @return true or false as string
     */
    public String getValue(String key) {
        try {
            return env.getProperty(key);
        } catch (Exception e) {
            logger.warn("can not get value of key: {} from environment, return false", key);
            return "false";
        }
    }

    public String getAdditionValue(String key) {
        return getValue("addition.datasource." + key);
    }

    public String getSpringValue(String key) {
        return getValue("spring.datasource." + key);
    }

    public String getDataValue(String key) {
        return getValue("data." + key);
    }

}
