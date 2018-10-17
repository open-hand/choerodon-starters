package io.choerodon.mybatis.util

import spock.lang.Specification

/**
 * Created by superlee on 2018/10/16.
 */
class SimpleTypeUtilSpec extends Specification {
    def "RegisterSimpleType"() {
        when: ""
        SimpleTypeUtil.registerSimpleType(SimpleTypeUtilSpec.class)
        then: ""
        true
    }

    def "RegisterSimpleType1"() {
        when: ""
        SimpleTypeUtil.registerSimpleType("io.choerodon.mybatis.util.SimpleTypeUtilSpec")
        then: ""
        true
    }

    def "IsSimpleType"() {
        when: ""
        def value = SimpleTypeUtil.isSimpleType(java.lang.Object.class)

        then: ""
        value == false
    }
}
