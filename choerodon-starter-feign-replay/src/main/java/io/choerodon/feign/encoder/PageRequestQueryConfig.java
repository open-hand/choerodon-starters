package io.choerodon.feign.encoder;

import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * feign PageRequest查询配置类
 *
 * @author superlee
 * @since 2019-07-02
 */
@Configuration
public class PageRequestQueryConfig {

    private ObjectFactory<HttpMessageConverters> messageConverters;

    public PageRequestQueryConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    @Primary
    public Encoder feignEncoder() {
        return new PageRequestQueryEncoder(new SpringEncoder(messageConverters));
    }
}
