package io.choerodon.core.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hailuo.liu@choerodon.io on 2019/8/8.
 * 此类用作高优先及参数设置，其他参数使用resources/choerodon-core-default.properties文件配置
 */
public class CoreEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * The name of default {@link PropertySource} defined in SpringApplication#configurePropertySources method.
     */
    public static final String PROPERTY_SOURCE_NAME = "defaultProperties";

    /**
     * The property name of "spring.main.allow-bean-definition-overriding".
     * Please refer to: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.1-Release-Notes#bean-overriding
     */
    public static final String ALLOW_BEAN_DEFINITION_OVERRIDING_PROPERTY = "spring.main.allow-bean-definition-overriding";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Object> defaultProperties = createDefaultProperties(environment);
        if (!CollectionUtils.isEmpty(defaultProperties)) {
            addOrReplace(propertySources, defaultProperties);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    private Map<String, Object> createDefaultProperties(ConfigurableEnvironment environment) {
        Map<String, Object> defaultProperties = new HashMap<String, Object>();
        setAllowBeanDefinitionOverriding(defaultProperties);
        return defaultProperties;
    }



    /**
     * Set {@link #ALLOW_BEAN_DEFINITION_OVERRIDING_PROPERTY "spring.main.allow-bean-definition-overriding"} to be
     * <code>true</code> as default.
     *
     * @param defaultProperties the default {@link Properties properties}
     * @see #ALLOW_BEAN_DEFINITION_OVERRIDING_PROPERTY
     * @since 2.7.1
     */
    private void setAllowBeanDefinitionOverriding(Map<String, Object> defaultProperties) {
        defaultProperties.put(ALLOW_BEAN_DEFINITION_OVERRIDING_PROPERTY, Boolean.TRUE.toString());
    }

    /**
     * Copy from BusEnvironmentPostProcessor#addOrReplace(MutablePropertySources, Map)
     *
     * @param propertySources {@link MutablePropertySources}
     * @param map             Default Dubbo Properties
     */
    private void addOrReplace(MutablePropertySources propertySources,
                              Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }
    }
}
