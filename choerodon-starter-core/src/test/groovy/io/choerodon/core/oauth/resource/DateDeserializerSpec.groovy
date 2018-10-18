package io.choerodon.core.oauth.resource

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import io.choerodon.core.exception.CommonException
import io.choerodon.core.infra.common.utils.SpockUtils
import io.choerodon.core.oauth.DetailsHelper
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([DetailsHelper])
class DateDeserializerSpec extends Specification {
    private DateDeserializer dateDeserializer = new DateDeserializer()

    def "Deserialize"() {
        given: "构造请求参数"
        JsonParser jsonParser = Mock(JsonParser)
        DeserializationContext deserializationContext = Mock(DeserializationContext)
        String format = "2018-12-12 12:12:12"

        and: "mock静态方法-CustomUserDetails"
        PowerMockito.mockStatic(DetailsHelper)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(SpockUtils.getCustomUserDetails())

        when: "调用方法"
        Date date = dateDeserializer.deserialize(jsonParser, deserializationContext)

        then: "校验结果"
        1 * jsonParser.getValueAsString() >> { format }
        date.format(("yyyy-MM-dd HH:mm:ss")).equals(format)

        when: "调用方法[异常]"
        date = dateDeserializer.deserialize(jsonParser, deserializationContext)

        then: "校验结果"
        1 * jsonParser.getValueAsString() >> { throw new CommonException("error") }
        date == null
    }
}
