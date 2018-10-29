package io.choerodon.statemachine;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author shinan.chen
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public class StateMachineApplicationContextHelper implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    ApplicationContext getContext() {
        return this.applicationContext;
    }
}
