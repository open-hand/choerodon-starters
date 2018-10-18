package io.choerodon.liquibase.excel

import io.choerodon.liquibase.addition.AdditionDataSource
import io.choerodon.liquibase.helper.LiquibaseHelper
import org.mockito.Mockito
import spock.lang.Specification

import java.sql.Connection

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
class DbAdaptorSpec extends Specification {
    private DbAdaptor dbAdaptor

    void setup() {
        ExcelDataLoader excelDataLoader = Mockito.mock(ExcelDataLoader)
        AdditionDataSource additionDataSource = Mockito.mock(AdditionDataSource)
        dbAdaptor = new DbAdaptor(excelDataLoader, additionDataSource)
        ad.getLiquibaseHelper() >> { new LiquibaseHelper() }
        helper.isSupportSequence() >> { true }
    }

    def "InitConnection"() {
        Connection connection = Mockito.mock(Connection)
        when:
        dbAdaptor.initConnection()

        then:
        !connection.getAutoCommit()
        1 * dataSource.getConnection() >> { connection }
    }

    def "CloseConnection"() {
    }

    def "GetConnection"() {
    }

    def "ProcessTableRow"() {
    }

    def "CheckExists"() {
    }

    def "DoUpdate"() {
    }

    def "DoInsert"() {
    }

    def "WeakInsert"() {
    }

    def "DoPostUpdate"() {
    }

    def "CheckTlExists"() {
    }

    def "DoInsertTl"() {
    }

    def "DoInsertTl1"() {
    }

    def "ConvertDataType"() {
    }

    def "PrepareInsertSql"() {
    }

    def "GetSeqNextVal"() {
    }

    def "SequencePk"() {
    }
}
