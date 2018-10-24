package io.choerodon.mybatis.helper

import io.choerodon.mapper.RoleDO
import io.choerodon.mybatis.IntegrationTestConfiguration
import io.choerodon.mybatis.domain.EntityColumn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/10/24.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class SqlHelperSpec extends Specification {
    def "GetBindValue"() {
        given:
        EntityColumn entityColumn = Mock(EntityColumn)
        when:
        String str = SqlHelper.getBindValue(entityColumn, "iam_user")
        then:
        str.contains("value='iam_user'/>")
    }

    def "InsertValuesColumns"() {
        when:
        String str = SqlHelper.insertValuesColumns(RoleDO.class, true, true, true)
        then:
        str.contains("#{assignable,javaType=java.lang.Boolean}")
    }

    def "UpdateSetColumns"() {
        when:
        String str = SqlHelper.updateSetColumns(RoleDO.class, "RoleDO", true, true)

        then:
        str.contains("creation_date = #{RoleDO.creationDate,")
    }

    def "ExampleOrderBy"() {
        when:
        String str = SqlHelper.exampleOrderBy(RoleDO.class)
        String whereStr = SqlHelper.exampleWhereClause()
        String updateStr = SqlHelper.updateByExampleWhereClause()

        then:
        str.contains("<if test=\"orderByClause != null\">order by")
        whereStr.contains("            <when test=\"criterion.listValue\">\n")
        updateStr.contains("            <when test=\"criterion.singleValue\">\n")
    }
}
