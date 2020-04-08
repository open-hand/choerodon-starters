package io.choerodon.liquibase;

import io.choerodon.liquibase.metadata.impl.MetadataDriverDelegate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
@Component
public class StartupRunner implements CommandLineRunner {
    @Value("${spring.h2.console.enabled:false}")
    boolean h2Console;
    @Value("${metadata.init:false}")
    boolean metadataInit;
    @Autowired
    LiquibaseExecutor liquibaseExecutor;
    @Autowired
    DataSource dataSource;


    @Override
    public void run(String... args) throws Exception {
        boolean success = liquibaseExecutor.execute(args);
        if (success && metadataInit) {
            MetadataDriverDelegate.syncMetadata(dataSource);
        }
        if (!h2Console) {
            if (success) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        }
    }
}
