package io.choerodon.liquibase.excel


import spock.lang.Specification

import java.lang.reflect.Method

/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
class ExcelDataLoaderSpec extends Specification {
    void setup() {
    }

    def "ToColIndex"() {
        expect: "校验结果"
        ExcelDataLoader.toColIndex(value) == result

        where: "多值检验"
        value | result
        "AA"  | 27
        "A"   | 1
    }

    def "GetFilePath"() {
        given: "准备上下文"
        ExcelDataLoader loader = new ExcelDataLoader()
        loader.setFilePath("/tmp/app.jar")

        when: "调用方法"
        def value = loader.getFilePath()

        then: "校验结果"
        value != null
    }

    def "SetFilePath"() {
        given: "准备上下文"
        ExcelDataLoader loader = new ExcelDataLoader()

        when: "调用方法"
        loader.setFilePath("/tmp/app.jar")


        then: "校验结果"
        loader.getFilePath() == "/tmp/app.jar"
    }

    def "GetUpdateExclusionMap"() {
        given: "准备上下文"
        ExcelDataLoader loader = new ExcelDataLoader()
        loader.setUpdateExclusionMap(new HashMap<String, Set<String>>())

        when: "调用方法"
        def value = loader.getUpdateExclusionMap()

        then: "校验结果"
        value.size() == 0
    }

    def "SetUpdateExclusionMap"() {
        given: "准备上下文"
        ExcelDataLoader loader = new ExcelDataLoader()

        when: "调用方法"
        loader.setUpdateExclusionMap(new HashMap<String, Set<String>>())


        then: "校验结果"
        loader.getUpdateExclusionMap().size() == 0
    }

    def "errorLog"() {
        given: "准备上下文"
        ExcelDataLoader loader = new ExcelDataLoader()
        List<TableData> tableData = new ArrayList<>()
        TableData data = new TableData()
        List<TableData.TableRow> tableRows = new ArrayList<>()
        TableData.TableRow row = new TableData.TableRow()
        row.setProcessFlag(true)
        tableRows.add(row)
        data.setTableRows(tableRows)
        tableData.add(data)

        Method errorLog = loader.getClass().getDeclaredMethod("errorLog", List.class)
        errorLog.setAccessible(true)

        when: "调用方法"
        def value = errorLog.invoke(loader, tableData)

        then: "校验结果"
        value == 1
    }
}
