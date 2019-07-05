package io.choerodon.config;

import io.choerodon.mybatis.ChoerodonMybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@inheritDoc}
 *
 * @author wuguokai
 */
@SpringBootApplication(exclude = ChoerodonMybatisAutoConfiguration.class)
public class ConfigToolApplication {
    /**
     * 主函数
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConfigToolApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

}
