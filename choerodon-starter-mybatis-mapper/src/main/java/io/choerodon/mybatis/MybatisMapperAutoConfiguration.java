package io.choerodon.mybatis;

import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.constant.CommonMapperConfigConstant;
import io.choerodon.mybatis.constant.DatabaseProductName;
import io.choerodon.mybatis.domain.Config;
import io.choerodon.mybatis.language.MultiLanguageInterceptor;
import io.choerodon.mybatis.pagehelper.Dialect;
import io.choerodon.mybatis.pagehelper.PageInterceptor;
import io.choerodon.mybatis.pagehelper.dialect.DialectHelper;
import io.choerodon.mybatis.pagehelper.dialect.MySqlDialect;
import io.choerodon.mybatis.pagehelper.dialect.OracleDialect;
import io.choerodon.mybatis.pagehelper.dialect.SqlServerDialect;
import io.choerodon.mybatis.spring.CommonMapperScannerConfigurer;
import io.choerodon.mybatis.spring.resolver.MethodArgParamResolverConfig;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


@Configuration
@Import(MethodArgParamResolverConfig.class)
public class MybatisMapperAutoConfiguration implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(MybatisMapperAutoConfiguration.class);

    private String datasourceUrl;

    public MybatisMapperAutoConfiguration() {
    }

    public MybatisMapperAutoConfiguration(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    /**
     * 配置扫描包路径
     *
     * @return MapperScannerConfigurer
     */
    @Bean
    @Primary
    public MapperScannerConfigurer mapperScannerConfigurer() {
        CommonMapperScannerConfigurer configurer = new CommonMapperScannerConfigurer();
        configurer.setBasePackage("*.**.mapper");
        Config config = configurer.getMapperHelper().getConfig();
        config.setSeqFormat("{3}_s.nextval");
        DbType dbType = DbType.MYSQL;
        if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_H2)) {
            dbType = DbType.H2;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_ORACLE)) {
            dbType = DbType.ORACLE;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_SQLSERVER)) {
            dbType = DbType.SQLSERVER;
        } else if (this.datasourceUrl.startsWith(CommonMapperConfigConstant.DB_URL_PREFIX_SAP)) {
            dbType = DbType.HANA;
        }
        config.setDbType(dbType);
        config.setBefore(dbType.isSupportSequence());
        config.setIdentity(dbType.getIdentity());
        return configurer;
    }

    /**
     * 配置支持的数据库方言以及分页、排序插件
     *
     * @param dataSource        dataSource
     * @param sqlSessionFactory sqlSessionFactory
     * @return Dialect
     * @throws SQLException SQLException
     */
    @Bean
    public Dialect dialect(DataSource dataSource, SqlSessionFactory sqlSessionFactory) throws SQLException {
        Dialect dialect = null;
        Connection connection = dataSource.getConnection();
        try {
            String productName = connection.getMetaData().getDatabaseProductName();
            if (DatabaseProductName.SQL_SERVER.value().equals(productName)) {
                dialect = new SqlServerDialect();
            } else if (DatabaseProductName.ORACLE.value().equals(productName)) {
                dialect = new OracleDialect();
            } else if (DatabaseProductName.MYSQL.value().equals(productName)) {
                dialect = new MySqlDialect();
            } else {
                logger.warn("未知数据库类型，默认使用MySQL方言。");
                dialect = new MySqlDialect();
            }
            DialectHelper.setDialect(dialect);
            PageInterceptor pageInterceptor = new PageInterceptor(dialect);
            pageInterceptor.setProperties(new Properties());
            sqlSessionFactory.getConfiguration().addInterceptor(pageInterceptor);

            MultiLanguageInterceptor multiLanguageInterceptor = new MultiLanguageInterceptor();
            sqlSessionFactory.getConfiguration().addInterceptor(multiLanguageInterceptor);
            //配置JdbcTypeForNull, oracle数据库必须配置，解决插入null的时候报错问题
            sqlSessionFactory.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        } catch (SQLException e) {
            logger.info("[sql exception]", e);
        } finally {
            connection.close();
        }
        return dialect;
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


    @Override
    public void setEnvironment(Environment environment) {
        datasourceUrl = environment.getProperty("spring.datasource.url");
    }
}