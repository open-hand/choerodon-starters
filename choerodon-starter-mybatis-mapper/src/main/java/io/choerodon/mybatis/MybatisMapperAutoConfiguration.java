package io.choerodon.mybatis;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.constant.CommonMapperConfigConstant;
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


@Configuration
@Import(MethodArgParamResolverConfig.class)
public class MybatisMapperAutoConfiguration  implements EnvironmentAware{

    private static final Logger logger = LoggerFactory.getLogger(MybatisMapperAutoConfiguration.class);

    private String datasourceUrl;

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
            if ("Microsoft SQL Server".equals(productName)) {
                dialect = new SqlServerDialect();
            } else if ("Oracle".equals(productName)) {
                dialect = new OracleDialect();
            } else if ("MySQL".equals(productName)) {
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

        } catch (SQLException e) {
            logger.info("[sql exception]" + e);
        } finally {
            connection.close();
        }
        return dialect;
    }

    /**
     * 配置zipkin插件，统计sql时长
     *
     * @param sqlSessionFactory sqlSessionFactory
     * @param spanAccessor      spanAccessor
     * @return ""
     */
    @Bean
    public String zipkinInterceptor(SqlSessionFactory sqlSessionFactory, SpanAccessor spanAccessor) {
        ZipkinInterceptor zipkinInterceptor = new ZipkinInterceptor(spanAccessor);
        sqlSessionFactory.getConfiguration().addInterceptor(zipkinInterceptor);
        return "";
    }

    @Override
    public void setEnvironment(Environment environment) {
        datasourceUrl = environment.getProperty("spring.datasource.url");
    }
}