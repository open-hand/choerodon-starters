package io.choerodon.freemarker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.Properties;

@ComponentScan
@Configuration
public class ChoerodonFreemarkerAutoConfiguration{

    @Bean("freemarkerConfig")
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPaths("classpath:/WEB-INF/view", "classpath:/WEB-INF/templates");
        freeMarkerConfigurer.setDefaultEncoding("UTF-8");
        freeMarkerConfigurer.setPreferFileSystemAccess(false);
        Properties properties = new Properties();
        properties.setProperty("auto_import", "spring.ftl as spring");
        properties.setProperty("template_update_delay", "2");
        properties.setProperty("number_format", "#");
        properties.setProperty("date_format", "yyyy-MM-dd");
        properties.setProperty("time_format", "HH:mm:ss");
        properties.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        freeMarkerConfigurer.setFreemarkerSettings(properties);
        return freeMarkerConfigurer;
    }

    @Bean("defaultFreeMarkerView")
    public FreeMarkerViewResolver defaultFreeMarkerView() {
        FreeMarkerViewResolver defaultFreeMarkerView = new FreeMarkerViewResolver();
        defaultFreeMarkerView.setViewClass(DefaultFreeMarkerView.class);
        defaultFreeMarkerView.setSuffix(".html");
        defaultFreeMarkerView.setOrder(0);
        setCommonFreeMarkerViewResolverConfig(defaultFreeMarkerView);
        return defaultFreeMarkerView;
    }

    public static void setCommonFreeMarkerViewResolverConfig(FreeMarkerViewResolver freeMarkerViewResolver) {
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        freeMarkerViewResolver.setRequestContextAttribute("base");
        freeMarkerViewResolver.setExposeRequestAttributes(true);
        freeMarkerViewResolver.setExposeSessionAttributes(true);
        freeMarkerViewResolver.setExposeSpringMacroHelpers(true);
        freeMarkerViewResolver.setAllowSessionOverride(true);
    }

}
