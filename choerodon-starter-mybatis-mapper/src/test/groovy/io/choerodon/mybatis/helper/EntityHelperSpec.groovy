package io.choerodon.mybatis.helper

import io.choerodon.mapper.RoleDO
import io.choerodon.mybatis.IntegrationTestConfiguration
import io.choerodon.mybatis.domain.EntityColumn
import io.choerodon.mybatis.domain.EntityTable
import org.powermock.reflect.Whitebox
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/10/24.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class EntityHelperSpec extends Specification {

    def "GetSelectColumns"() {
        when:
        String str = EntityHelper.getSelectColumns(RoleDO.class)
        then:
        str.contains("creation_date AS creationDate")

        when: "测试dealByGeneratedValueStrategy"
        EntityTable entityTable = Mock(EntityTable)
        EntityColumn entityColumn = Mock(EntityColumn)
        GeneratedValue generatedValue = Mock(GeneratedValue)
        generatedValue.strategy() >> GenerationType.IDENTITY
        generatedValue.generator() >> "MySQL"
        Whitebox.invokeMethod(EntityHelper.class, "dealByGeneratedValueStrategy", entityTable, entityColumn, generatedValue)

        then:
        noExceptionThrown()
    }
}
