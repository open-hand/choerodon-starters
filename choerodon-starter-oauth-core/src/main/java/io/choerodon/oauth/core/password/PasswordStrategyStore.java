package io.choerodon.oauth.core.password;

import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuguokai
 */
public class PasswordStrategyStore {
    private final Map<String, PasswordStrategy> strategyMap = new HashMap<>();

   private ApplicationContext context;

    public PasswordStrategyStore(ApplicationContext context) {
        this.context = context;
    }
    @PostConstruct
    private void init() {
        Map<String, PasswordStrategy> passwordStrategyMap = context.getBeansOfType(PasswordStrategy.class);
        for (Map.Entry<String, PasswordStrategy> entry : passwordStrategyMap.entrySet()) {
            String type = entry.getValue().getType();
            strategyMap.put(type, entry.getValue());
        }
    }

    public Map<String, PasswordStrategy> getStrategyMap() {
        return strategyMap;
    }

    public PasswordStrategy getProvider(String key){
        return strategyMap.get(key);
    }
}
