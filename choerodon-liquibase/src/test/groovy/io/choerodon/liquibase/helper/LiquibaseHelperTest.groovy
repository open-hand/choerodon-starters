package io.choerodon.liquibase.helper

import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-18
 *
 */
class LiquibaseHelperTest extends Specification {
    def "DbType"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper(url)

        when: "调用方法"
        def value = helper.dbType()

        then: "校验结果"
        value == type

        where: "多次测试"
        url                                         | type
        "jdbc:h2:mem:testdb;"                       | LiquibaseHelper.DbType.H2
        "jdbc:mysql://localhost:3306/iam_service"   | LiquibaseHelper.DbType.MYSQL
        "jdbc:oracle:thin:@192.168.99.100:49161:xe" | LiquibaseHelper.DbType.ORACLE
        "jdbc:sqlserver"                            | LiquibaseHelper.DbType.SQLSERVER
        "jdbc:sap"                                  | LiquibaseHelper.DbType.HANA
        "adasd"                                     | null
    }

    def "Get db type multi-times"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper("jdbc:h2:mem:testdb;")

        when: "调用方法"
        helper.dbType()
        def value = helper.dbType()

        then: "校验结果"
        value == LiquibaseHelper.DbType.H2
    }

    def "Test isSupportAutoIncrement() for DbType"() {
        when: "调用方法"
        def value = type.isSupportAutoIncrement()

        then: "校验结果"
        value == result

        where: "多次测试"
        result | type
        true   | LiquibaseHelper.DbType.H2
        true   | LiquibaseHelper.DbType.MYSQL
        false  | LiquibaseHelper.DbType.ORACLE
        true   | LiquibaseHelper.DbType.SQLSERVER
        false  | LiquibaseHelper.DbType.HANA
        false  | LiquibaseHelper.DbType.DB2
    }

    def "IsSupportSequence"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper("jdbc:mysql://localhost:3306/iam_service")

        when: "调用方法"
        def value = helper.isSupportSequence()

        then: "校验结果"
        !value

        where: "多次测试"
        url                                         | type
        "jdbc:h2:mem:testdb;"                       | LiquibaseHelper.DbType.H2
        "jdbc:mysql://localhost:3306/iam_service"   | LiquibaseHelper.DbType.MYSQL
        "jdbc:oracle:thin:@192.168.99.100:49161:xe" | LiquibaseHelper.DbType.ORACLE
        "jdbc:sqlserver"                            | LiquibaseHelper.DbType.SQLSERVER
        "jdbc:sap"                                  | LiquibaseHelper.DbType.HANA
        "adasd"                                     | null
    }

    def "IsH2Base"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper(url)

        when: "调用方法"
        def value = helper.isH2Base()

        then: "校验结果"
        value == result

        where: "多次测试"
        url                                       | result
        "jdbc:h2:mem:testdb;"                     | true
        "jdbc:mysql://localhost:3306/iam_service" | false
    }

    def "IsOracle"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper(url)

        when: "调用方法"
        def value = helper.isOracle()

        then: "校验结果"
        value == result

        where: "多次测试"
        url                                         | result
        "jdbc:mysql://localhost:3306/iam_service"   | false
        "jdbc:oracle:thin:@192.168.99.100:49161:xe" | true
    }

    def "IsMysql"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper(url)

        when: "调用方法"
        def value = helper.isMysql()

        then: "校验结果"
        value == result

        where: "多次测试"
        url                                       | result
        "jdbc:mysql://localhost:3306/iam_service" | true
        "jdbc:sap"                                | false
    }

    def "IsSqlServer"() {
        given: "准备初始化"
        LiquibaseHelper helper = new LiquibaseHelper("jdbc:sqlserver")

        when: "调用方法"
        def value = helper.isSqlServer()

        then: "校验结果"
        value

        where: "多次测试"
        url              | result
        "jdbc:sqlserver" | true
        "jdbc:sap"       | false
    }
}
