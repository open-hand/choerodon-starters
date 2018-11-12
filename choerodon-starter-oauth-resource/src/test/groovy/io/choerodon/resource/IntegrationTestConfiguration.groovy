package io.choerodon.resource

import io.choerodon.resource.config.ChoerodonResourceServerConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@TestConfiguration
@Import([ResourceServerConfig, ChoerodonResourceServerConfiguration])
class IntegrationTestConfiguration {

}
