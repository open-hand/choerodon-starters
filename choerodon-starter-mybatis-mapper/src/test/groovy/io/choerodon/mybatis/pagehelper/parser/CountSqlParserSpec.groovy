package io.choerodon.mybatis.pagehelper.parser

import spock.lang.Specification

/**
 * Created by superlee on 2018/10/18.
 */
class CountSqlParserSpec extends Specification {
    CountSqlParser countSqlParser = new CountSqlParser()

    def "getSimpleCountSql"() {
        when:
        def value = countSqlParser.getSimpleCountSql("select * from iam_user")
        then:
        value.contains('select count(0) from (')
    }

}
