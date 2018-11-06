package io.choerodon.eureka.event;

import io.choerodon.eureka.event.endpoint.EurekaEventEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EurekaEventProperties.class)
public class EurekaEventAutoConfiguration {

    @Bean
    public EurekaEventEndpoint eurekaEventEndpoint(AbstractEurekaEventObserver eurekaEventObserver) {
        return new EurekaEventEndpoint(eurekaEventObserver);
    }

}
