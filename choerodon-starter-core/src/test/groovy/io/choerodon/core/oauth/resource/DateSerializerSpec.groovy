package io.choerodon.core.oauth.resource

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
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
class DateSerializerSpec extends Specification {
    private DateSerializer dateSerializer = new DateSerializer()

    def "Serialize"() {
        given: "构造请求参数"
        Date date = new Date()
        JsonGenerator jsonGenerator = Mock(JsonGenerator)
        SerializerProvider serializerProvider = null

        and: "mock静态方法-CustomUserDetails"
        PowerMockito.mockStatic(DetailsHelper)
        PowerMockito.when(DetailsHelper.getUserDetails()).thenReturn(SpockUtils.getCustomUserDetails())

        when: "调用方法"
        dateSerializer.serialize(date, jsonGenerator, serializerProvider)

        then: "校验结果"
        1 * jsonGenerator.writeString(_)

        when: "调用方法[异常]"
        dateSerializer.serialize(date, jsonGenerator, serializerProvider)

        then: "校验结果"
        1 * jsonGenerator.writeString(_) >> { throw new CommonException("error") }
        1 * jsonGenerator.writeNull()
    }
}
