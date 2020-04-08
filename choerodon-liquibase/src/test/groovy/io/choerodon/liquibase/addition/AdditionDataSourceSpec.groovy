package io.choerodon.liquibase.addition

import spock.lang.Specification
import spock.lang.Stepwise

/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
@Stepwise
class AdditionDataSourceSpec extends Specification {

    def "new"() {
        when: "调用方法"
        def ad = new AdditionDataSource()

        then: "校验结果"
        ad != null
    }

    def "constructor with args"() {
        when: "调用方法"
        def ad = new AdditionDataSource("jdbc:h2:mem:testdb;", "sa", "sa", "ss", false, true)

        then: "校验结果"
        ad != null
    }

    def "get null datasource"() {
        given: "设置数据源"
        def ad = new AdditionDataSource("jdbc:h2:mem:testdb;", "sa", "sa", "ss", false, null, true)

        when: "调用方法"
        def ds = ad.getDataSource()

        then: "校验结果"
        ds != null
    }
}
