package io.choerodon.statemachine.config;

import io.choerodon.statemachine.ClientProcessor;
import io.choerodon.statemachine.StateMachineApplicationContextHelper;
import io.choerodon.statemachine.dto.PropertyData;
import io.choerodon.statemachine.endpoint.ClientEndpoint;
import io.choerodon.statemachine.service.ClientService;
import io.choerodon.statemachine.service.impl.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/22
 */
@Configuration
public class StateMachineConfiguration {

    @Bean
    public StateMachineApplicationContextHelper stateMachineApplicationContextHelper() {
        return new StateMachineApplicationContextHelper();
    }

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean("stateMachinePropertyData")
    public PropertyData stateMachinePropertyData() {
        PropertyData stateMachinePropertyData = new PropertyData();
        stateMachinePropertyData.setServiceName(serviceName);
        return stateMachinePropertyData;
    }

    @Bean("clientService")
    public ClientService clientService() {
        return new ClientServiceImpl();
    }

    @Bean
    public ClientEndpoint clientEndpoint() {
        return new ClientEndpoint(stateMachinePropertyData());
    }

    @Bean("ClientProcessor")
    public ClientProcessor clientProcessor(StateMachineApplicationContextHelper stateMachineApplicationContextHelper, PropertyData stateMachinePropertyData) {
        return new ClientProcessor(stateMachineApplicationContextHelper, stateMachinePropertyData);
    }
}
