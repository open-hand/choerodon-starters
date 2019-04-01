package io.choerodon.dataset;

import io.choerodon.mybatis.common.CustomProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

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
}
