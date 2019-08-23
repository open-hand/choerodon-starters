package io.choerodon.websocket.helper

import io.choerodon.websocket.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by hailuo.liu@choerodon.io on 2019-08-23.
 */
@Import(IntegrationTestConfiguration)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WebSocketHelperSpec extends Specification {
    void setup() {
    }

    void cleanup() {
    }

    def "SendMessageByKey"() {
    }

    def "SendMessageBySession"() {
    }

    def "Subscribe"() {
    }

    def "Unsubscribe"() {
    }
}
