package io.choerodon.liquibase.excel


import spock.lang.Specification
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

    def "ProcessData"() {
//        given: "准备上下文"
//        ExcelDataLoader loader = new ExcelDataLoader()
//        AdditionDataSource additionDataSource = Mock(AdditionDataSource)
//        additionDataSource.getLiquibaseHelper() >> { Mock(LiquibaseHelper) }
////        additionDataSource. >> { Mock(Connection) }
//        DataSource dataSource = Mock(DataSource)
//        dataSource.getConnection() >> { Mock(Connection) }
//        DbAdaptor dbAdaptor = new DbAdaptor(loader, additionDataSource)
//        dbAdaptor.setDataSource(dataSource)
//        loader.dbAdaptor = dbAdaptor
//        loader.setUpdateExclusionMap(new HashMap<String, Set<String>>())
//
//        when: "调用方法"
//        def value = loader.processData()
//
//        then: "校验结果"
//        value.size() == 0
    }

    def "ProcessTable"() {
    }

    def "UpdateCellFormula"() {
    }

    def "TryUpdateCell"() {
    }

    def "FindCell"() {
    }

    def "Execute"() {

    }
}
