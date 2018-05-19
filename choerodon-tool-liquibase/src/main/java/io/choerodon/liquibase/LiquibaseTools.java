package io.choerodon.liquibase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;


/**
 * Created by xausky on 4/6/17.
 */
@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class LiquibaseTools {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LiquibaseTools.class);
        app.setWebEnvironment(false);
        app.run(args);
    }
}
