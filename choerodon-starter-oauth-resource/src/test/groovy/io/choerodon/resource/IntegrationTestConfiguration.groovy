package io.choerodon.resource

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@TestConfiguration
@Import([ResourceServerConfig])
class IntegrationTestConfiguration {

}
