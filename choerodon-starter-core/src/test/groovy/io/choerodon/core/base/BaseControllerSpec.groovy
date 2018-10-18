package io.choerodon.core.base

import org.springframework.web.bind.WebDataBinder
import spock.lang.Specification

/**
 * @author dengyouquan
 * */

class BaseControllerSpec extends Specification {
    private BaseController baseController = new BaseController()

    def "InitBinder"() {
        given: "构造请求参数"
        WebDataBinder binder = Mock(WebDataBinder)

        when: "调用方法"
        baseController.initBinder(binder)

        then: "校验结果"
        noExceptionThrown()
        1 * binder.registerCustomEditor(_, _)
    }
}
