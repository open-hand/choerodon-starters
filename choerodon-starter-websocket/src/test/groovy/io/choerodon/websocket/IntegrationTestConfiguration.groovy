package io.choerodon.websocket

import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
class IntegrationTestConfiguration {
    static int redisPort = 0
    @BeforeClass
    static void before() {

//        SpringApplicationBuilder recvie = new SpringApplicationBuilder(TestApplication.class)
//        recvie.run()
    }
}