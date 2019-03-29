package io.choerodon.freemarker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE, classes = [TestApplication])
class FreemarkerSpec extends Specification{
    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer
    @Autowired
    FreeMarkerBeanProvider freeMarkerBeanProvider

    def "Auto Configuration" () {
        when:
        Map<String, Object> beans = freeMarkerBeanProvider.getAvailableBean()
        then:
        freeMarkerConfigurer != null
        beans.size() == 1
    }
}
