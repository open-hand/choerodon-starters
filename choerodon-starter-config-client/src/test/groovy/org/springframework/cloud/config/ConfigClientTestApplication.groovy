package org.springframework.cloud.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ConfigClientTestApplication {

    static void main(String[] args) {
        SpringApplication.run(ConfigClientTestApplication.class, args)
    }

}
