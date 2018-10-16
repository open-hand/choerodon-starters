package io.choerodon.mybatis

import org.apache.ibatis.session.SqlSessionFactory
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DatabaseMetaData

/**
 * Created by superlee on 2018/10/16.
 */
class MybatisMapperAutoConfigurationSpec extends Specification {

    def "MapperScannerConfigurer"() {
        given: ""
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration("jdbc:h2")

        when: ""
        def configurer = mybatisMapperAutoConfiguration.mapperScannerConfigurer()

        then: ""
        true
    }

    def "Dialect"() {
        given: ""
        def dataSource = Mock(DataSource)
        def sqlSessionFactory = Mock(SqlSessionFactory)
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration()
        def connection = Mock(Connection)
        dataSource.getConnection() >> connection
        def databaseMetaData = Mock(DatabaseMetaData)
        connection.getMetaData() >> databaseMetaData
        databaseMetaData.getDatabaseProductName() >> "MySQL"

        when: ""
        mybatisMapperAutoConfiguration.dialect(dataSource, sqlSessionFactory)

        then: ""

    }

    def "ZipkinInterceptor"() {
    }

    def "GetDatabaseIdProvider"() {
    }

    def "SetEnvironment"() {
    }
}
