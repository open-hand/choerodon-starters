package io.choerodon.liquibase.excel


import org.apache.poi.ss.usermodel.Sheet
import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
class TableDataSpec extends Specification {
//    TableData data = new TableData()
    List<TableData> tables = new ArrayList<>()

    void setup() {
        ExcelSeedDataReader dataReader = new ExcelSeedDataReader(this.getClass().getClassLoader().getResourceAsStream("script/db/2018-03-27-init-data.xlsx"))
        tables = dataReader.load()
    }

    def "GetStartLine"() {
        when: "调用方法"
        def value = tables.get(0).getStartLine()

        then: "校验结果"
        value != 0
    }

    def "SetStartLine"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setStartLine(7)

        then: "校验结果"
        data.getStartLine() == 7
    }

    def "GetStartCol"() {
        when: "调用方法"
        def value = tables.get(0).getStartCol()

        then: "校验结果"
        value != 0
    }

    def "SetStartCol"() {
        when: "调用方法"
        tables.get(0).setStartCol(4)

        then: "校验结果"
        tables.get(0).getStartCol() == 4
    }

    def "GetTableRows"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getTableRows()

        then: "校验结果"
        value.size() != 0
    }

    def "SetTableRows"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setTableRows(new ArrayList<TableData.TableRow>())

        then: "校验结果"
        data.getTableRows().size() == 0
    }

    def "GetName"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getName()

        then: "校验结果"
        value != null
    }

    def "SetName"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setName("IAM_SYSTEM_SETTING")

        then: "校验状态"
        data.getName() == "IAM_SYSTEM_SETTING"
    }

    def "GetUniqueColumns"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getUniqueColumns()

        then: "校验结果"
        value.size() != 0
    }

    def "SetUniqueColumns"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setUniqueColumns(new ArrayList<TableData.Column>())

        then: "校验结果"
        data.getUniqueColumns().size() == 0
    }

    def "GetColumns"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getColumns()

        then: "校验结果"
        value.size() != 0
    }

    def "SetColumns"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setColumns(new ArrayList<TableData.Column>())

        then: "校验结果"
        data.getColumns().size() == 0
    }

    def "GetInsert"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getInsert()

        then: "校验结果"
        value == 0
    }

    def "SetInsert"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setInsert(5)

        then: "校验结果"
        data.getInsert() != 0
    }

    def "GetLangs"() {
        given: "准备上下文"
        TableData data = tables.get(0)
        Set<String> langs = new HashSet<>(1)
        langs.add("zh_CN")
        data.setLangs(langs)

        when: "调用方法"
        def value = data.getLangs()

        then: "校验结果"
        value.size() != 0
    }

    def "SetLangs"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setLangs(new HashSet<String>())

        then: "校验结果"
        data.getLangs().size() == 0
    }

    def "Complete"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.complete()

        then: "校验结果"
        !value
    }

    def "ProcessSummary"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.processSummary()

        then: "校验结果"
        value != null
    }

    def "GetSheet"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        Sheet value = data.getSheet()

        then: "校验结果"
        value.getLastRowNum() != 0
    }

    def "SetSheet"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.setSheet(Mock(Sheet))

        then: "校验结果"
        data.getSheet() != null
    }

    def "AddCol"() {
        given: "准备上下文"
        TableData data = tables.get(0)
        def originalSize = data.getColumns().size()

        when: "调用方法"
        data.addCol(Mock(TableData.Column))

        then: "校验结果"
        data.getColumns().size() == originalSize + 1
    }

    def "MakeReady"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        data.makeReady()

        then: "校验结果"
        noExceptionThrown()
    }

    def "GetSummaryInfo"() {
        given: "准备上下文"
        TableData data = tables.get(0)

        when: "调用方法"
        def value = data.getSummaryInfo()

        then: "校验结果"
        value != null
    }
}
