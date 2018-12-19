package io.choerodon.liquibase.addition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 读取环境变量
 *
 * @author dongfan117@gmail.com
 */
public class ProfileMap implements EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(ProfileMap.class);

    private RelaxedPropertyResolver springDatasourceProperty;
    private RelaxedPropertyResolver dataProperty;
    private RelaxedPropertyResolver additionDatasourceProperty;
    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.springDatasourceProperty = new RelaxedPropertyResolver(env, "spring.datasource.");
        this.dataProperty = new RelaxedPropertyResolver(env, "data");
        this.additionDatasourceProperty = new RelaxedPropertyResolver(env, "custom.datasource");
        this.env = env;
    }

    /**
     * 获取环境变量.
     *
     * @param key key
     * @return
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

    public RelaxedPropertyResolver getSpringDatasourceProperty() {
        return springDatasourceProperty;
    }

    public void setSpringDatasourceProperty(RelaxedPropertyResolver springDatasourceProperty) {
        this.springDatasourceProperty = springDatasourceProperty;
    }

    public RelaxedPropertyResolver getDataProperty() {
        return dataProperty;
    }

    public void setDataProperty(RelaxedPropertyResolver dataProperty) {
        this.dataProperty = dataProperty;
    }

    public RelaxedPropertyResolver getAdditionDatasourceProperty() {
        return additionDatasourceProperty;
    }

    public void setAdditionDatasourceProperty(RelaxedPropertyResolver additionDatasourceProperty) {
        this.additionDatasourceProperty = additionDatasourceProperty;
    }
}
