package io.choerodon.mybatis.helper

import io.choerodon.mapper.RoleMapper
import io.choerodon.mybatis.domain.Config
import io.choerodon.mybatis.domain.EntityColumn
import io.choerodon.mybatis.domain.EntityTable
import io.choerodon.mybatis.provider.base.BaseInsertProvider
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.scripting.LanguageDriver
import org.apache.ibatis.session.Configuration
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.powermock.reflect.Whitebox
import org.spockframework.runtime.Sputnik
import spock.lang.Specification


/**
 * Created by superlee on 2018/10/23.
 */
@PrepareForTest([MappedStatement.class])
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class MapperTemplateSpec extends Specification {


    def "processKeyGeneratorWithSequence"() {
        given:
        MapperHelper mapperHelper = Mock(MapperHelper)
        Config config = Mock(Config)
        config.getSeqFormat() >> "{3}_s.nextval"
        mapperHelper.getConfig() >> config
        BaseInsertProvider baseInsertProvider = new BaseInsertProvider(null, mapperHelper)
        MappedStatement ms = PowerMockito.mock(MappedStatement.class)

        EntityColumn entityColumn = Mock(EntityColumn)
        entityColumn.getColumn() >> "id"
        entityColumn.getProperty() >> "id"
        EntityTable table = Mock(EntityTable)
        table.getName() >> "iam_role"
        entityColumn.getTable() >> table
        String keyId = "io.choerodon.RoleMapper.insert"
        Class clazz = RoleMapper.class
        Configuration configuration = Mock(Configuration)
        configuration.getDefaultScriptingLanuageInstance()>>Mock(LanguageDriver)
        boolean executeBefore = true
        String identity = "SEQUENCE"

        when:
        Whitebox.invokeMethod(baseInsertProvider, "processKeyGeneratorWithSequence", ms, entityColumn, keyId, clazz, configuration, executeBefore, identity)

        then:
        noExceptionThrown()
    }
}
