package io.choerodon.liquibase.excel

import io.choerodon.liquibase.addition.AdditionDataSource
import io.choerodon.liquibase.helper.LiquibaseHelper
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.support.membermodification.MemberModifier
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.stream.Collectors

/**
 *
 * @author zmf
 * @since 2018-10-17
 *
 */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([DbAdaptor, Connection, ExcelDataLoader])
class DbAdaptorSpec extends Specification {
    private DbAdaptor dbAdaptor
    ExcelDataLoader excelDataLoader = PowerMockito.mock(ExcelDataLoader)
    AdditionDataSource additionDataSource = PowerMockito.mock(AdditionDataSource)
    LiquibaseHelper liquibaseHelper = PowerMockito.mock(LiquibaseHelper)
    DataSource dataSource = PowerMockito.mock(DataSource)
    Connection connection = PowerMockito.mock(Connection)
    PreparedStatement preparedStatement = PowerMockito.mock(PreparedStatement)
    @Shared
    List<TableData> tables

    void setup() {
        ExcelSeedDataReader dataReader = new ExcelSeedDataReader(this.getClass().getClassLoader().getResourceAsStream("script/db/2018-03-27-init-data.xlsx"))
        tables = dataReader.load()
        PowerMockito.when(additionDataSource.getLiquibaseHelper()).thenReturn(liquibaseHelper)
        PowerMockito.when(additionDataSource.getDataSource()).thenReturn(dataSource)
        PowerMockito.when(liquibaseHelper.isSupportSequence()).thenReturn(true)
        PowerMockito.when(additionDataSource.getDataSource()).thenReturn(dataSource)
        PowerMockito.when(dataSource.getConnection()).thenReturn(connection)
        PowerMockito.when(connection.prepareStatement(Mockito.any(String))).thenReturn(preparedStatement)
        PowerMockito.when(preparedStatement.executeUpdate()).thenReturn(1)

        dbAdaptor = PowerMockito.spy(new DbAdaptor(excelDataLoader, additionDataSource))
    }

    private Iterable<TableData.TableRow> fetchTables() {
        ExcelSeedDataReader dataReader = new ExcelSeedDataReader(this.getClass().getClassLoader().getResourceAsStream("script/db/2018-03-27-init-data.xlsx"))
        tables = dataReader.load()
        def list = tables.stream()
                .map({ table -> table.getTableRows() })
                .flatMap({ rows -> rows.stream() })
                .collect(Collectors.toList()).asImmutable()
        list.asImmutable()
    }

    private static Method prepareMethod(Class aClass, String methodName, Class... params) {
        Method method = aClass.getDeclaredMethod(methodName, params)
        method.setAccessible(true)
        return method
    }

//    private Connection connection() {
//        def sourceUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=Mysql;TRACE_LEVEL_SYSTEM_OUT=2;"
//        def user="sa"
//        def key="sa"
//        Class.forName("org.h2.Driver")
//        return DriverManager.getConnection(sourceUrl, user, key)
//    }

    def "ProcessTableRow"() {
        given: "准备上下文"
        TableData.TableRow tableRow = Mock(TableData.TableRow)
        PowerMockito.doReturn(1L).when(dbAdaptor, "checkExists", tableRow)
        PowerMockito.doReturn(1).when(dbAdaptor, "doInsertTl", tableRow)

        when: "调用方法"
        def value = dbAdaptor.processTableRow(tableRow)

        then: "校验结果"
        value == 1
    }

    def "CheckExists"() {
    }


    def "DoUpdate"() {
        given: "初始化"
        PowerMockito.doReturn(false).when(dbAdaptor, "excluded", "test", new HashSet())
        Method doUpdate = prepareMethod(dbAdaptor.getClass(), "doUpdate", TableData.TableRow.class, Set.class, Set.class)
        MemberModifier.field(DbAdaptor, "connection").set(dbAdaptor, connection)

        when: "调用方法"
        doUpdate.invoke(dbAdaptor, tableRow, new HashSet<String>(), new HashSet<String>())

        then: "校验结果"
        noExceptionThrown()

        where: "多次执行"
        tableRow << fetchTables()
    }

    def "processLog"() {
        given: "初始化"
        TableData.TableRow tableRow = tables.get(2).getTableRows().get(0)
        def processLog = prepareMethod(DbAdaptor, "processLog", TableData.TableRow, TableData.TableCellValue)

        when: "调用方法"
        def value = processLog.invoke(dbAdaptor, tableRow, tableRow.getTableCellValues().get(1))

        then: "校验结果"
        value != null
    }

    def "getUnpresentFormulaTds"() {
        given: "初始化"
        TableData.TableRow tableRow = Mock(TableData.TableRow)
        def getUnpresentFormulaTds = prepareMethod(DbAdaptor, "getUnpresentFormulaTds", TableData.TableRow)
        List<TableData.TableCellValue> tableCellValues = new ArrayList<>()
        TableData.TableCellValue tableCellValue1 = Mock(TableData.TableCellValue)
        TableData.TableCellValue tableCellValue2 = Mock(TableData.TableCellValue)
        tableCellValue1.isValuePresent() >> { false }
        tableCellValue2.isValuePresent() >> { true }
        tableCellValue1.isFormula() >> { true }
        tableCellValue2.isFormula() >> { false }
        tableCellValues.add(tableCellValue1)
        tableCellValues.add(tableCellValue2)
        tableRow.getTableCellValues() >> { tableCellValues }

        when: "调用方法"
        List<TableData.TableCellValue> values = (List<TableData.TableCellValue>) getUnpresentFormulaTds.invoke(dbAdaptor, tableRow)

        then: "校验结果"
        values.size() == 1
    }

    def "weakInsert"() {
        given: "初始化"
        PowerMockito.doReturn(null).when(dbAdaptor, "checkExists", Mockito.any(TableData.TableRow))
        PowerMockito.doReturn(1).when(dbAdaptor, "doInsertTl", Mockito.any(TableData.TableRow))
        PowerMockito.doReturn(1L).when(dbAdaptor, "doInsert", Mockito.any(TableData.TableRow))
        ExcelDataLoader loader = new ExcelDataLoader()
        loader.tables = tables
        MemberModifier.field(DbAdaptor, "dataProcessor").set(dbAdaptor, loader)

        when: "调用方法"
        dbAdaptor.weakInsert(tables)

        then: "校验结果"
        thrown(RuntimeException)
    }

    def "DoPostUpdate"() {
        given: "初始化"
        TableData.TableRow tableRow = tables.get(0).getTableRows().get(0)
        def doPostUpdate = prepareMethod(DbAdaptor, "doPostUpdate", TableData.TableRow, TableData.TableCellValue, Long)
        TableData.TableCellValue tableCellValue = Mock(TableData.TableCellValue)
        TableData.Column column = Mock(TableData.Column)
        tableCellValue.getColumn() >> { column }
        column.getName() >> { "name" }
        MemberModifier.field(DbAdaptor, "connection").set(dbAdaptor, connection)
        Long value = 5L

        when: "调用方法"
        doPostUpdate.invoke(dbAdaptor, tableRow, tableCellValue, value)

        then: "校验结果"
        noExceptionThrown()
    }

    def "convert normally"() {
        given: "初始化"
        def convertDataType = prepareMethod(DbAdaptor, "convertDataType", String, String)

        when: "调用方法"
        convertDataType.invoke(dbAdaptor, value, type)

        then: "校验结果"
        noExceptionThrown()

        where: "多次测试"
        value        | type
        "124"        | "DECIMAL"
        "123.122"    | "NUMBER"
        "2017-10-22" | "DATE"
        ""           | "NUMBER"
    }

    def "convertDataType with exception"() {
        given: "初始化"
        def convertDataType = prepareMethod(DbAdaptor, "convertDataType", String, String)

        when: "调用方法"
        convertDataType.invoke(dbAdaptor, "2017-1", "DATE")

        then: "校验结果"
        thrown(InvocationTargetException)
    }

    def "getSeqNextVal"() {
        given: "初始化"
        def getSeqNextVal = prepareMethod(DbAdaptor, "getSeqNextVal", String)
        MemberModifier.field(DbAdaptor, "connection").set(dbAdaptor, connection)
        ResultSet resultSet = PowerMockito.mock(ResultSet)
        PowerMockito.when(preparedStatement.executeQuery()).thenReturn(resultSet)
        PowerMockito.when(resultSet.getLong(1)).thenReturn(1L)

        when: "调用方法"
        def result = getSeqNextVal.invoke(dbAdaptor, "FD_ORGANIZATION")

        then: "校验结果"
        result == 1L
    }

    def "getSeqNextVal with exception"() {
        given: "初始化"
        def getSeqNextVal = prepareMethod(DbAdaptor, "getSeqNextVal", String)
        MemberModifier.field(DbAdaptor, "connection").set(dbAdaptor, connection)
        ResultSet resultSet = PowerMockito.mock(ResultSet)
        PowerMockito.when(preparedStatement.executeQuery()).thenReturn(resultSet)
        PowerMockito.when(resultSet.getLong(1)).thenThrow(Mock(SQLException))

        when: "调用方法"
        getSeqNextVal.invoke(dbAdaptor, "FD_ORGANIZATION")

        then: "校验结果"
        thrown(InvocationTargetException)
    }
}
