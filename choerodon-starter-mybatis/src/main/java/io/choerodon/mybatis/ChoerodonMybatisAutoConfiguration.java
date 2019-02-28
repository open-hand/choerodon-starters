package io.choerodon.mybatis;

import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.interceptor.AuditInterceptor;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)
@PropertySource("classpath:default-choerodon-mybatis-config.properties")
public class ChoerodonMybatisAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactories;

    @Autowired(required = false)
    private List<CustomProvider> customProviders;

    @PostConstruct
    public void setLanguageProvider() {
        if (customProviders == null || customProviders.isEmpty()){
            LOGGER.warn("请实现 CustomProvider 接口以提供当前语言。");
        } else {
            OGNL.customProvider = customProviders.get(0);
        }
    }

    @PostConstruct
    public void addAuditInterceptor(){
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories){
            sqlSessionFactory.getConfiguration().addInterceptor(new AuditInterceptor());
        }
    }

}
