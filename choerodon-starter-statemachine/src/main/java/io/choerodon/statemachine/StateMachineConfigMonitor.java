package io.choerodon.statemachine;

import io.choerodon.statemachine.dto.InvokeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class StateMachineConfigMonitor {

    private StateMachineConfigMonitor() {
    }

    private static final Logger logger = LoggerFactory.getLogger(StateMachineConfigMonitor.class);
    public static final Map<String, InvokeBean> configInvokeBeanMap = new HashMap<>();
    public static final Map<String, InvokeBean> updateStatusBeanMap = new HashMap<>();

    /**
     * 校验code不能重复
     */
    static void checkConfigUniqueCode(String code) {
        Set<Map.Entry<String, InvokeBean>> invokes = configInvokeBeanMap.entrySet();
        invokes.forEach(x -> {
            if (x.getValue().getCode().equals(code)) {
                logger.error("StateMachineConfigMonitor annotation configCode duplication: {}", code);
//                throw new IllegalArgumentException("error.checkUniqueCode.duplication");
            }
        });
    }

    static void checkUpdateUniqueCode(String code) {
        Set<Map.Entry<String, InvokeBean>> invokes = updateStatusBeanMap.entrySet();
        invokes.forEach(x -> {
            if (x.getValue().getCode().equals(code)) {
                logger.error("StateMachineConfigMonitor annotation updateCode duplication: {}", code);
//                throw new IllegalArgumentException("error.checkUniqueCode.duplication");
            }
        });
    }
}
