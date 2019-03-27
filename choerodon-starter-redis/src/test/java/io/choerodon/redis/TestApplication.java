package io.choerodon.redis;

import io.choerodon.message.IMessagePublisher;
import io.choerodon.mybatis.common.CustomProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.Set;

@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public CustomProvider testCustomProvider() {
        return new CustomProvider(){
            @Override
            public String currentLanguage() {
                return "en_GB";
            }

            @Override
            public Long currentPrincipal() {
                return 1133L;
            }

            @Override
            public Set<String> getSupportedLanguages() {
                return Collections.singleton("en_GB");
            }
        };
    }

    @Bean
    public IMessagePublisher iMessagePublisher(){
        return new IMessagePublisher(){

            @Override
            public void publish(String channel, Object message) {

            }

            @Override
            public void rPush(String list, Object message) {

            }

            @Override
            public void message(String name, Object message) {

            }
        };
    }
}
