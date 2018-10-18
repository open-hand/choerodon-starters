package io.choerodon.mybatis.pagehelper.dialect

import io.choerodon.core.domain.PageInfo
import io.choerodon.mybatis.pagehelper.cache.Cache
import io.choerodon.mybatis.pagehelper.cache.CacheFactory
import org.apache.ibatis.cache.CacheKey
import org.apache.ibatis.mapping.BoundSql
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/18.
 */
@PrepareForTest(CacheFactory.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class SqlServerDialectSpec extends Specification {

    SqlServerDialect dialect = new SqlServerDialect()

    def setup() {
        Properties properties = Mock(Properties)
        properties.getProperty(_) >> "sql"
        PowerMockito.mockStatic(CacheFactory.class)
        Cache cache = Mock(Cache)
        PowerMockito.when(CacheFactory.createCache(Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(cache)
        dialect.setProperties(properties)
    }

    def "GetCountSql"() {
        given:
        BoundSql boundSql = Mock(BoundSql)
        boundSql.getSql() >> "select * from iam_role order by id"

        when:
        def value = dialect.getCountSql(null, boundSql, null, null, null)
        then:
        "SELECT count(0) FROM iam_role" == value
    }

    def "GetPageSql"() {
        given:
        CacheKey cacheKey = Mock(CacheKey)
        PageInfo pageInfo = new PageInfo(1, 5)
        String sql = "select * from iam_role order by id"

        when:
        def value = dialect.getPageSql(sql, pageInfo, cacheKey)
        then:
        value.contains("SELECT TOP 5 * FROM (")
    }


}
