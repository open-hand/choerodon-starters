package io.choerodon.mybatis.domain

import io.choerodon.mybatis.MapperException
import org.powermock.reflect.Whitebox
import spock.lang.Specification

/**
 * Created by superlee on 2018/10/24.
 */
class ConfigSpec extends Specification {

    def "registerNewType"() {
        given:
        Config config = new Config()
        Properties properties = Mock(Properties)
        properties.getProperty("simpleTypes") >>"aaa"
        properties.getProperty("style")>>"bbb"
        when:
        Whitebox.invokeMethod(config, "registerNewType", properties)
        then:
        thrown(MapperException)
    }
}
