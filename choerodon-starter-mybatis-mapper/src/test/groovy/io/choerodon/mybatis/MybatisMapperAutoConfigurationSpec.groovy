package io.choerodon.mybatis

import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.core.env.Environment
import spock.lang.Specification

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DatabaseMetaData
/**
 * Created by superlee on 2018/10/16.
 */
class MybatisMapperAutoConfigurationSpec extends Specification {

    def "MapperScannerConfigurer"() {
        given: 
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration("jdbc:h2")

        when:
        def configurer = mybatisMapperAutoConfiguration.mapperScannerConfigurer()

        then:
        true
    }

    def "Dialect"() {
        given:
        def dataSource = Mock(DataSource)
        def sqlSessionFactory = Mock(SqlSessionFactory)
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration()
        def connection = Mock(Connection)
        dataSource.getConnection() >> connection
        def databaseMetaData = Mock(DatabaseMetaData)
        connection.getMetaData() >> databaseMetaData
        databaseMetaData.getDatabaseProductName() >> "MySQL"
        def configuration = Mock(Configuration)
        sqlSessionFactory.getConfiguration() >> configuration

        when:
        mybatisMapperAutoConfiguration.dialect(dataSource, sqlSessionFactory)

        then:
        true

    }

    def "GetDatabaseIdProvider"() {
        given:
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration()
        when:
        mybatisMapperAutoConfiguration.getDatabaseIdProvider()
        then:
        true
    }

    def "SetEnvironment"() {
        when:
        def mybatisMapperAutoConfiguration = new MybatisMapperAutoConfiguration()
        mybatisMapperAutoConfiguration.setEnvironment(Mock(Environment))
        then:
        true
    }
}
