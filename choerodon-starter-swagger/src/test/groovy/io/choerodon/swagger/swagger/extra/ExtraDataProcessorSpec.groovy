package io.choerodon.swagger.swagger.extra

import io.choerodon.swagger.annotation.ChoerodonExtraData
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.core.annotation.AnnotationUtils
import spock.lang.Specification

@RunWith(PowerMockRunner.class)
@PrepareForTest([AnnotationUtils.class])
@PowerMockRunnerDelegate(Sputnik.class)
class ExtraDataProcessorSpec extends Specification {
    private ExtraDataProcessor extraDataProcessor = new ExtraDataProcessor()

    def "PostProcessBeforeInitialization"() {
        when: "方法调用"
        def mock = Mock(Object)
        def initialization = extraDataProcessor.postProcessBeforeInitialization(mock, "beanName")
        then: "无异常抛出"
        initialization == mock
    }

    def "PostProcessAfterInitialization"() {
        given: "参数准备"
        def bean = Mock(ExtraDataManager)
        bean.getData() >> {
            def extraData = new ExtraData()
            extraData.setData(null)
            extraData.put("key", "value")
            return extraData
        }

        and: "mock静态方法"
        PowerMockito.mockStatic(AnnotationUtils.class)
        PowerMockito.when(AnnotationUtils.findAnnotation(bean.getClass(), ChoerodonExtraData.class)).thenReturn(Mock(ChoerodonExtraData))

        when: "方法调用"
        extraDataProcessor.postProcessAfterInitialization(bean, "beanName")
        then: "无异常抛出"
        noExceptionThrown()
        extraDataProcessor.getExtraData().toString() != null
        extraDataProcessor.getExtraData().getData().get("key").equals("value")
    }

}
