package io.choerodon.liquibase.excel

import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-19
 *
 */
class ExcelSeedDataReaderSpec extends Specification {
    void setup() {
    }

    def "Load"() {
        given: "初始化"
        ExcelSeedDataReader dataReader = new ExcelSeedDataReader(this.getClass().getClassLoader().getResourceAsStream("script/db/2018-03-27-init-data.xlsx"))

        when: "调用方法"
        def tables = dataReader.load()

        then: "校验结果"
        tables.size() != 0
    }
}
