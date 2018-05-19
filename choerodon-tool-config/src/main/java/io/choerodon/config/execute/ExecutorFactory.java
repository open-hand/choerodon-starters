package io.choerodon.config.execute;

import io.choerodon.config.utils.ServiceType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * 存储配置文件执行器工厂
 *
 * @author wuguokai
 */
public class ExecutorFactory implements ApplicationContextAware {

    private static Map<ServiceType, Function<ApplicationContext, Executor>> parserFactory =
            new EnumMap<>(ServiceType.class);

    private ApplicationContext applicationContext;

    static {
        parserFactory.put(ServiceType.API_GATEWAY, ApiGatewayExecutor::new);
        parserFactory.put(ServiceType.DEAFAULT, DefaultExecutor::new);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据服务类型返回不同的执行器
     *
     * @param serviceType 服务类型
     * @return Executor
     */
    public Executor getExecutor(ServiceType serviceType) {
        return Optional.ofNullable(parserFactory.get(serviceType))
                .map(o -> o.apply(this.applicationContext))
                .orElseThrow(() -> new IllegalArgumentException("当前仅支持yml与proprerties文件"));
    }
}
