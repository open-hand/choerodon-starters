package io.choerodon.feign

import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class CustomRibbonConfigurationTest extends Specification {
    def "RibbonRule"() {
        given: "初始化配置"
        CustomRibbonConfiguration configuration = new CustomRibbonConfiguration()

        when: "调用方法"
        def value = configuration.ribbonRule()

        then: "校验结果"
        value instanceof CustomMetadataRule
    }
}
