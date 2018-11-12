package io.choerodon.mybatis

import io.choerodon.resource.annoation.EnableChoerodonResourceServer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Created by superlee on 2018/10/17.
 */
@SpringBootApplication
@EnableChoerodonResourceServer
class TestApplication {
    static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args)
    }
}
