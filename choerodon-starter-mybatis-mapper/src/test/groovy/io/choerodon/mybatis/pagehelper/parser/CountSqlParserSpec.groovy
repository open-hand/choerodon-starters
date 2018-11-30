package io.choerodon.mybatis.pagehelper.parser

import net.sf.jsqlparser.statement.select.FromItem
import net.sf.jsqlparser.statement.select.Join
import net.sf.jsqlparser.statement.select.LateralSubSelect
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SelectBody
import net.sf.jsqlparser.statement.select.SetOperationList
import net.sf.jsqlparser.statement.select.SubJoin
import net.sf.jsqlparser.statement.select.SubSelect
import net.sf.jsqlparser.statement.select.WithItem
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

    def "sqlToCount"() {
        given:
        Select select = Mock(Select)
        SelectBody selectBody = Mock(SelectBody)
        select.getSelectBody() >> selectBody
//        selectBody instanceof PlainSelect >> false
        when:
        countSqlParser.sqlToCount(select)
        then:
        thrown(MissingMethodException)
    }

    def "processSelectBody"() {
        given:
        SelectBody selectBody = Mock(WithItem)
        selectBody.getSelectBody() >> Mock(SetOperationList)
        when:
        countSqlParser.processSelectBody(selectBody)
        then:
        noExceptionThrown()
    }

    def "processFromItem"() {
        given:
        SubJoin subJoin = Mock(SubJoin)
        Join join = Mock(Join)
        subJoin.getJoin() >> join
        FromItem fromItem = Mock(SubJoin)
        join.getRightItem() >> fromItem
        SubSelect subSelect = Mock(SubSelect)
        fromItem.getLeft() >> subSelect
        when:
        countSqlParser.processFromItem(subJoin)
        then:
        noExceptionThrown()

        when:
        LateralSubSelect lateralSubSelect = Mock(LateralSubSelect)
        countSqlParser.processFromItem(lateralSubSelect)
        then:
        noExceptionThrown()
    }

}
