package io.choerodon.config.execute;

import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * 除api-gateway之外的服务使用的执行器
 *
 * @author wuguokai
 */
public class DefaultExecutor extends AbstractExector {
    DefaultExecutor(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    /**
     * 直接返回原map
     *
     * @param map 属性键值对集合
     * @return map
     */
    @Override
    public Map<String, Object> executeInternal(Map<String, Object> map) {
        return map;
    }
}
