package io.choerodon.mybatis.pagehelper.page

import io.choerodon.mybatis.pagehelper.Select
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/23.
 */
class PageMethodSpec extends Specification {
    def "Count"() {
        given:
        Select select = Mock(Select)

        when:
        long count = PageMethod.count(select)
        then:
        count == 0
    }
}
