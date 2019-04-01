package io.choerodon.mybatis;

import io.choerodon.mybatis.common.CustomProvider;
import io.choerodon.mybatis.entity.DbType;
import io.choerodon.mybatis.interceptor.AuditInterceptor;
import io.choerodon.mybatis.interceptor.MultiLanguageInterceptor;
import io.choerodon.mybatis.interceptor.OvnInterceptor;
import io.choerodon.mybatis.interceptor.SequenceInterceptor;
import io.choerodon.mybatis.util.OGNL;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)
@PropertySource("classpath:default-choerodon-mybatis-config.properties")
public class ChoerodonMybatisAutoConfiguration {

    @Value("${db.type}")
    private String dbType;

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactories;

    @Autowired(required = false)
    private CustomProvider customProvider;

    @Autowired
    private List<Interceptor> interceptors;

    @PostConstruct
    public void setLanguageProvider() {
        if (customProvider == null){
            LOGGER.warn("请实现 CustomProvider 接口以提供当前语言。");
        } else {
            OGNL.customProvider = customProvider;
        }
    }
}
