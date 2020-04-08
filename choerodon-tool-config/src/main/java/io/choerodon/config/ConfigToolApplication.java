package io.choerodon.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.choerodon.mybatis.MybatisMapperAutoConfiguration;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
@SpringBootApplication(exclude = MybatisMapperAutoConfiguration.class)
public class ConfigToolApplication {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConfigToolApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

}
