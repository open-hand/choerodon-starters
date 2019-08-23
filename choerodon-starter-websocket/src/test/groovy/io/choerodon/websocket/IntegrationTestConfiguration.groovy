package io.choerodon.websocket

import ai.grakn.redismock.RedisServer
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
class IntegrationTestConfiguration {


    @BeforeClass
    public void before() {
        //启动Redis mock
        RedisServer server = RedisServer.newRedisServer(6370);
        server.start();
    }

}