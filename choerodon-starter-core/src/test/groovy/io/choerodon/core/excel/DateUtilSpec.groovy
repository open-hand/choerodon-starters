package io.choerodon.core.excel

import org.springframework.util.StringUtils
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class DateUtilSpec extends Specification {
    def "String2Date[null]"() {
        when: "调用方法"
        String value = null
        String format = null
        Date date = DateUtil.string2Date(value, format)
        then: "校验结果"
        date == null
        noExceptionThrown()


        when: "调用方法"
        value = "20181016 174600"
        format = null
        date = DateUtil.string2Date(value, format)
        then: "校验结果"
        date == null
        noExceptionThrown()
    }

    def "String2Date"() {
        when: "调用方法"
        Date date = DateUtil.string2Date(value, format)
        then: "校验结果"
        date != null
        noExceptionThrown()
        where: ""
        value            | format
        "20181016174600" | "yyyyMMddHHmmss"
        "201810161746"   | "yyyyMMddHHmm"
        "2018101617"     | "yyyyMMddHH"
        "20181016"       | "yyyyMMdd"
        "201810"         | "yyyyMM"
    }

    def "FormatDate"() {
        when: "调用方法"
        result = DateUtil.formatDate(date, format)
        then: "校验结果"
        noExceptionThrown()
        where: ""
        date            | format        || result
        ""              | ""            || ""
        "--"            | ""            || ""
        "20181016 17"   | ""            || ""
        "2018101617"    | "yyyyMMddHH"  || "2018101617"
        "20181016"      | ""            || "2018年10月16日"
    }
}
