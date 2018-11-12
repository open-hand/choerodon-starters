package io.choerodon.mybatis.code

import spock.lang.Specification

/**
 * Created by superlee on 2018/10/24.
 */
class DbTypeSpec extends Specification {
    def "GetByValue"() {
        given:

        when:
        DbType dbType = DbType.getByValue("mysql")
        dbType.setIdentity("JDBC")

        then:
        dbType.getValue().equals(DbType.MYSQL.getValue())
        dbType.supportAutoIncrement

    }
}
