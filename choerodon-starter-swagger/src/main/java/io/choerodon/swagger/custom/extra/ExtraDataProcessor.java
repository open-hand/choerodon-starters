package io.choerodon.swagger.custom.extra;

import io.choerodon.swagger.annotation.ChoerodonExtraData;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author wuguokai
 */
public class ExtraDataProcessor implements BeanPostProcessor {

    private ExtraData extraData = null;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        ChoerodonExtraData choerodonExtraData = AnnotationUtils.findAnnotation(bean.getClass(), ChoerodonExtraData.class);
        if (choerodonExtraData != null && bean instanceof ExtraDataManager) {
            //DONE 获取类中的数据并加入到swagger json里
            ExtraData newExtraData = ((ExtraDataManager) bean).getData();
            if (newExtraData != null) {
                this.extraData = newExtraData;
            }
        }
        return bean;
    }

    public ExtraData getExtraData() {
        return extraData;
    }
}
