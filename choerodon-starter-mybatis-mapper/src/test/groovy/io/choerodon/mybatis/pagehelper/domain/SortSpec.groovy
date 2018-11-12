package io.choerodon.mybatis.pagehelper.domain

import io.choerodon.mybatis.domain.EntityField
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/23.
 */
class SortSpec extends Specification {
    Sort sort = new Sort("id")

    def "and"() {
        given:
        Sort sort1 = new Sort("name")
        sort.equals(sort1)
        sort.hashCode()

        when:
        Sort value = sort.and(sort1)
        Sort.Order order1 = value.getOrderFor("name")
        order1.toString()

        then:
        order1 != null
        order1.isAscending()
        !order1.isDescending()
        !order1.isIgnoreCase()
        !order1.isPropertyChanged()

        when:"调用entityField的equal方法"
        EntityField field1 = new EntityField(null,null)
        EntityField field2 = new EntityField(null,null)

        then:
        field1.equals(field2)
    }

}
