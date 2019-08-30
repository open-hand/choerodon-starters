package io.choerodon.message;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:choerodon-message-default-config.properties")
@Import({MessageRedisAutoConfiguration.class, MessageRabbitAutoConfiguration.class})
public class MessageAutoConfiguration {
}
