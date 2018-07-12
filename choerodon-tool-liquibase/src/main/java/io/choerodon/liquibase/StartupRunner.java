package io.choerodon.liquibase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
@Component
public class StartupRunner implements CommandLineRunner {
    @Value("${spring.h2.console.enabled:false}")
    boolean h2Console;

    @Autowired
    LiquibaseExecutor liquibaseExecutor;

    @Override
    public void run(String... args) throws Exception {
        boolean success = liquibaseExecutor.execute(args);
        if (!h2Console) {
            if (success) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        }

    }
}
