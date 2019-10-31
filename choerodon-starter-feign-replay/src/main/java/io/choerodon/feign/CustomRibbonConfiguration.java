package io.choerodon.feign;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;


/**
 *{@inheritDoc}
 * @author crock
 */
@Configurable
public class CustomRibbonConfiguration {

    @Autowired(required = false)
    private IClientConfig config;
    @Autowired
    private CommonProperties commonProperties;

    /**
     * {@inheritDoc}
     */
    @Bean
    public IRule ribbonRule() {
        CustomMetadataRule rule = new CustomMetadataRule();
        rule.initWithNiwsConfig(config);
        rule.setCommonProperties(commonProperties);
        return rule;
    }

}
