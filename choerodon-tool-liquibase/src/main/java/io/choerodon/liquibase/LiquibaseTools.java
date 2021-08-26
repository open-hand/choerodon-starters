package io.choerodon.liquibase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by xausky on 4/6/17.
 */
@SpringBootApplication
public class LiquibaseTools {


    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(LiquibaseTools.class);
            app.run(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
