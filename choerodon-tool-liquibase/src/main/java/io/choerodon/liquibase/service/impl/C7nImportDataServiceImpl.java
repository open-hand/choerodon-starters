package io.choerodon.liquibase.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.helper.generator.core.config.InstallerConfigProperties;
import org.hzero.helper.generator.core.domain.entity.Config;
import org.hzero.helper.generator.core.domain.entity.Mapping;
import org.hzero.helper.generator.core.infra.mapper.InitDataMapper;
import org.hzero.helper.generator.core.infra.util.XmlUtils;
import org.hzero.helper.generator.installer.dto.DataSourceDTO;
import org.hzero.helper.generator.installer.service.impl.ImportDataServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/8/23
 * @Modified By:
 */
@Service
@Primary
public class C7nImportDataServiceImpl extends ImportDataServiceImpl {
    private static final String ORACLE = "oracle";
    private static final String MYSQL = "mysql";
    private static final String SQLSERVER = "sqlserver";
    private static final String POSTGRESQL = "postgresql";
    private Map<String, Config> schema_marge = XmlUtils.SCHEMA_MERGE;

    private static final Logger LOGGER = LoggerFactory.getLogger(C7nImportDataServiceImpl.class);
    @Autowired
    InitDataMapper initDataMapper;
    @Autowired
    InstallerConfigProperties configProperties;
    @Value("${spring.datasource.dynamic.datasource.gen.url}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.gen.username}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.gen.password}")
    private String password;

    @Override
    public DataSourceDTO reconstructDsInfo(Mapping mapping) {

        String schema = mapping.getSchema();
        String dbUrl = this.url;
        String username = this.username;
        String password = this.password;
        if (configProperties != null && configProperties.getDbSource() != null &&
                configProperties.getDbSource().getDatasources() != null && mapping.getEnv() != null) {
            Map<String, String> datasource = configProperties.getDbSource().getDatasources().get(mapping.getEnv());
            if (datasource != null) {
                dbUrl = datasource.get("url");
                username = datasource.get("username");
                password = datasource.get("password");
            }
        }
        Config config = getConfig(dbUrl);
        assert config != null;
        if (StringUtils.equals(config.getMerge(), "true")) {
            schema = StringUtils.defaultIfBlank(config.getTargetSchema(), schema);
        }
        if (StringUtils.equals(config.getName(), MYSQL)) {
            // todo 覆盖该类 唯一自定义逻辑
            dbUrl = dbUrl.replace("/?", "?");
            dbUrl = StringUtils.replace(dbUrl, "?", "/" + schema + "?");
        } else if (StringUtils.equals(config.getName(), SQLSERVER)) {
            dbUrl += "DatabaseName=" + schema;
        } else if (StringUtils.equals(config.getName(), ORACLE) && StringUtils.equals(config.getMerge(), "false")) {
            username = StringUtils.defaultIfBlank(mapping.getUsername(), schema);
            password = StringUtils.defaultIfBlank(mapping.getPassword(), schema);
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>> username : {} | password : {}", username, password);
        } else if (StringUtils.equals(POSTGRESQL, config.getName())) {
            dbUrl = dbUrl + "?currentSchema=" + schema;
        }
        DataSourceDTO dataSourceDTO = new DataSourceDTO();
        dataSourceDTO.setDbUrl(dbUrl);
        dataSourceDTO.setUsername(username);
        dataSourceDTO.setPassword(password);
        return dataSourceDTO;
    }

    /**
     * 获取config
     *
     * @param url 数据库url
     * @return config
     */
    private Config getConfig(String url) {
        if (StringUtils.contains(url, MYSQL)) {
            return schema_marge.get(MYSQL);
        } else if (StringUtils.contains(url, SQLSERVER)) {
            return schema_marge.get(SQLSERVER);
        } else if (StringUtils.contains(url, ORACLE)) {
            return schema_marge.get(ORACLE);
        } else if (StringUtils.contains(url, POSTGRESQL)) {
            return schema_marge.get(POSTGRESQL);
        }
        return null;
    }

}
