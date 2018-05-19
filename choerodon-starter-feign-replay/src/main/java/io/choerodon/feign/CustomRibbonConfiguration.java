package io.choerodon.feign;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;


/**
 *{@inheritDoc}
 * @author crock
 */
public class CustomRibbonConfiguration {

    @Autowired(required = false)
    private IClientConfig config;

    /**
     * {@inheritDoc}
     */
    @Bean
    public IRule ribbonRule() {
        CustomMetadataRule rule = new CustomMetadataRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }

}
