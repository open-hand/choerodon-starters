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
        given:
        CustomRibbonConfiguration configuration = new CustomRibbonConfiguration()

        when:
        def value = configuration.ribbonRule()

        then:
        value instanceof CustomMetadataRule
    }
}
