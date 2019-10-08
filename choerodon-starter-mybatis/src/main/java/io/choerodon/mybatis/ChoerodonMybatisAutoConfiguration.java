package io.choerodon.mybatis;

import io.choerodon.mybatis.autoconfigure.MapperOverrideProperties;
import io.choerodon.base.provider.CustomProvider;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.*;
import tk.mybatis.mapper.autoconfigure.MybatisProperties;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Configuration
@ComponentScan
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@MapperScan(basePackages = "io.choerodon.**.mapper")
@PropertySource("classpath:default-choerodon-mybatis-config.properties")
public class ChoerodonMybatisAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChoerodonMybatisAutoConfiguration.class);

    @Autowired(required = false)
    private CustomProvider customProvider;

    @PostConstruct
    public void setLanguageProvider() {
        if (customProvider == null) {
            LOGGER.warn("请实现 CustomProvider 接口以提供当前语言。");
        } else {
            OGNL.customProvider = customProvider;
        }
    }

    @Bean
    @Primary
    public MybatisProperties mybatisProperties(){
        return new MapperOverrideProperties();
    }


    /**
     * 自动识别使用的数据库类型
     * 在mapper.xml中databaseId的值就是跟这里对应，
     * 如果没有databaseId选择则说明该sql适用所有数据库
     */
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        properties.setProperty("SQL Server", "sqlserver");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}
