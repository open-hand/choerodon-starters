package io.choerodon.fragment.autoconfigure;

import org.hzero.fragment.service.FragmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.choerodon.fragment.service.impl.FragmentC7nServiceImpl;

/**
 * 包扫描
 *
 * @author scp
 */
@Configuration
@ComponentScan(basePackages = "io.choerodon.fragment")
public class FragmentC7nAutoConfig {

    @Bean
    public FragmentService transferService() {
        return new FragmentC7nServiceImpl();
    }

}