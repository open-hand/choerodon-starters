package org.springframework.cloud.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.config.client.*
import org.springframework.cloud.context.refresh.ContextRefresher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@TestConfiguration
@Import([ConfigClientAutoConfiguration, ConfigServiceBootstrapConfiguration, DiscoveryClientConfigServiceBootstrapConfiguration])
class IntegrationTestConfiguration {

    @Bean
    ConfigClientWatch configClientWatch(ContextRefresher contextRefresher) {
        return new ConfigClientWatch(contextRefresher)
    }

    @Bean
    ConfigServerInstanceProvider configServerInstanceProvider(
            DiscoveryClient discoveryClient) {
        return new ConfigServerInstanceProvider(discoveryClient)
    }
}
