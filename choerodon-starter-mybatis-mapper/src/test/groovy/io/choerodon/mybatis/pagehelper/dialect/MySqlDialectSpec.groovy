package io.choerodon.mybatis.pagehelper.dialect

import io.choerodon.core.domain.PageInfo
import org.apache.ibatis.cache.CacheKey
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/23.
 */
class MySqlDialectSpec extends Specification {
    def "GetPageSql"() {
        given:
        MySqlDialect mySqlDialect = new MySqlDialect()
        PageInfo pageInfo = new PageInfo(1, 5)
        when:
        String sql = mySqlDialect.getPageSql("select * from user", pageInfo, Mock(CacheKey))
        then:
        sql.contains(" LIMIT ")
    }
}
