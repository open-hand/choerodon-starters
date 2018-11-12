package io.choerodon.mybatis.helper

import io.choerodon.mybatis.domain.EntityColumn
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.reflection.MetaObject
import org.apache.ibatis.session.Configuration
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import java.sql.Statement

/**
 * Created by superlee on 2018/10/23.
 */
@PrepareForTest(MappedStatement.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class SelectKeyGeneratorSpec extends Specification {

    def "processBefore"() {
        given:
        MappedStatement mappedStatement = PowerMockito.mock(MappedStatement.class)
        SelectKeyGenerator selectKeyGenerator = new SelectKeyGenerator(mappedStatement, true)
        Executor executor = Mock(Executor)
        Statement statement = Mock(Statement)
        Object parameter = new Object()
        String[] str = ["", "a"]
        PowerMockito.when(mappedStatement.getKeyProperties()).thenReturn(str)

        Configuration configuration = Mock(Configuration)
        PowerMockito.when(mappedStatement.getConfiguration()).thenReturn(configuration)
        MetaObject metaObject = Mock(MetaObject)
        configuration.newMetaObject(_) >> metaObject
        metaObject.hasGetter(_) >> true
        metaObject.hasSetter(_) >> true
        metaObject.getValue(_) >> new Object()


        Executor keyExecutor = Mock(Executor)
        configuration.newExecutor(_, _) >> keyExecutor
        List<Object> list = new ArrayList<>()
        list << new Object()
        keyExecutor.query(_, _, _, _) >> list

        when:
        selectKeyGenerator.processBefore(executor, mappedStatement, statement, parameter)
        then:
        noExceptionThrown()


        when:
        EntityColumn entityColumn1 = new EntityColumn()
        EntityColumn entityColumn2 = new EntityColumn()
        entityColumn1.equals(entityColumn2)

        SelectKeyGenerator selectKeyGenerator1 = new SelectKeyGenerator(mappedStatement, false)
        selectKeyGenerator1.processAfter(executor, mappedStatement, statement, parameter)

        then:
        noExceptionThrown()
    }
}
