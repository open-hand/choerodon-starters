package io.choerodon.freemarker;

import io.choerodon.freemarker.annotation.FreeMarkerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author shengyang.zhou@hand-china.com
 */
@Component
public class FreeMarkerBeanProvider {

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, Object> beanMap;

    public Map<String, Object> getAvailableBean() {
        if (beanMap == null) {
            beanMap = applicationContext.getBeansWithAnnotation(FreeMarkerBean.class);
        }
        return beanMap;
    }

}
