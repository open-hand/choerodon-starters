package io.choerodon.mybatis.util

import io.choerodon.mybatis.pagehelper.domain.Sort
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/16.
 */
class SqlSafeUtilSpec extends Specification {

    def "Validate"() {
        given:""
        def order = new Sort.Order(Sort.Direction.ASC, "name")
        def sort = new Sort(order)

        when:""
        SqlSafeUtil.validate(sort)

        then:""
        true

        when:""
        def order1 = new Sort.Order(Sort.Direction.DESC, "select * from iam")
        def sort1 = new Sort(order1)
        SqlSafeUtil.validate(sort1)
        then:""
        thrown(IllegalArgumentException)
    }
}
