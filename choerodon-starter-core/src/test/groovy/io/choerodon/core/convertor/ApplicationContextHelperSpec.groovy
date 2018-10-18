package io.choerodon.core.convertor

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.context.support.AbstractRefreshableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.StaticApplicationContext
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class ApplicationContextHelperSpec extends Specification {
    private ApplicationContextHelper applicationContextHelper = new ApplicationContextHelper()

    def "SetApplicationContext"() {
        given: "构造请求参数"
        AbstractRefreshableApplicationContext abstractRefreshableApplicationContext =
                new ClassPathXmlApplicationContext("beans.xml")
        GenericApplicationContext genericApplicationContext =
                new StaticApplicationContext()

        when: "调用方法"
        applicationContextHelper.setApplicationContext(abstractRefreshableApplicationContext)

        then: "校验结果"
        noExceptionThrown()

        when: "调用方法"
        applicationContextHelper.setApplicationContext(genericApplicationContext)

        then: "校验结果"
        noExceptionThrown()

        when: "调用GetSpringFactory方法"
        def factory = ApplicationContextHelper.getSpringFactory()

        then: "校验结果"
        noExceptionThrown()

        when: "调用GetContext方法"
        def context = ApplicationContextHelper.getContext()

        then: "校验结果"
        noExceptionThrown()
    }
}
