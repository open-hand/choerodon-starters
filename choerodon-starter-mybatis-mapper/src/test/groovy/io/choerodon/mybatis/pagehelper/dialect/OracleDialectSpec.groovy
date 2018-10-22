package io.choerodon.mybatis.pagehelper.dialect

import io.choerodon.core.domain.PageInfo
import org.apache.ibatis.cache.CacheKey
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/18.
 */
class OracleDialectSpec extends Specification {

    def "GetPageSql"() {
        given:
        OracleDialect dialect = new OracleDialect()
        String sql = ""
        PageInfo pageInfo = new PageInfo(1,10)
        CacheKey cacheKey = Mock(CacheKey)

        when:
        String str = dialect.getPageSql(sql, pageInfo, cacheKey)

        then:
        str.contains('SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM (')

    }
}
