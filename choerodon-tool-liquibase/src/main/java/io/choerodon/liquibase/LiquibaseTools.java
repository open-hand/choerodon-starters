package io.choerodon.liquibase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by xausky on 4/6/17.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.hzero.installer.*"}, basePackageClasses = io.choerodon.liquibase.StartupRunner.class)
@MapperScan(basePackages = "org.hzero.installer.mapper")
public class LiquibaseTools {


    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(LiquibaseTools.class);
            app.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
