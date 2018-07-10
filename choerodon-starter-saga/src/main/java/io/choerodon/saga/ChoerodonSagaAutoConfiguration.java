package io.choerodon.saga;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChoerodonSagaProperties.class)
public class ChoerodonSagaAutoConfiguration {

    private ChoerodonSagaProperties choerodonSagaProperties;

    @Autowired
    public ChoerodonSagaAutoConfiguration(ChoerodonSagaProperties choerodonSagaProperties) {
        this.choerodonSagaProperties = choerodonSagaProperties;
    }



}
