package io.choerodon.config.builder

import io.choerodon.config.utils.ConfigFileFormat
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class BuilderFactorySpec extends Specification {
    def "ConfigFileFormat"() {
        when: "调用方法"
        for (ConfigFileFormat format : ConfigFileFormat.values()) {
            ConfigFileFormat.fromString(format.getValue())
        }

        then: "校验结果"
        !ConfigFileFormat.isValidFormat("value")
        ConfigFileFormat.isValidFormat(ConfigFileFormat.YAML.getValue())

        when: "调用方法[异常]"
        ConfigFileFormat.fromString("")

        then: "校验结果"
        def exception = thrown(IllegalArgumentException)
        exception.message.equals("value can not be empty")

        when: "调用方法[异常]"
        ConfigFileFormat.fromString("error")

        then: "校验结果"
        exception = thrown(IllegalArgumentException)
        exception.message.equals("error can not map enum")
    }

    def "GetBuilder"() {
        when: "调用方法"
        BuilderFactory.getBuilder(ConfigFileFormat.YML)
        BuilderFactory.getBuilder(ConfigFileFormat.PROPERTIES)

        then: "校验结果"
        noExceptionThrown()
    }
}
