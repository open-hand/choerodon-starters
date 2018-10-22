package org.springframework.cloud.config.client

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.cloud.config.environment.Environment
import org.springframework.cloud.config.environment.PropertySource
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigServicePropertySourceLocatorSpec extends Specification {
    ConfigServicePropertySourceLocator configServicePropertySourceLocator
    ConfigClientProperties defaultProperties = Mock(ConfigClientProperties)

    void setup() {
        configServicePropertySourceLocator = new ConfigServicePropertySourceLocator(defaultProperties)
    }

    def "Locate[Exception]"() {
        given: "方法参数准备"
        def environment = Mock(org.springframework.core.env.Environment)
        and: "mock"
        defaultProperties.override(environment) >> {
            def properties = Mock(ConfigClientProperties)
            properties.getUsername() >> { return "userName" }
            properties.getPassword() >> { return "password" }
            properties.getAuthorization() >> { return "bearer 5a6c39ea-0619-419e-832f-a8e05222108c" }
            properties.getHeaders() >> {
                def headers = new HashMap<String, String>()
                return headers
            }
            return properties
        }
        when: "方法调用"
        configServicePropertySourceLocator.locate(environment)
        then: "无异常抛出"
        def e = thrown(IllegalStateException)
        e.message == "You must set either 'password' or 'authorization'"
    }

    def "Locate1"() {
        given: "方法参数准备"
        def environment = Mock(org.springframework.core.env.Environment)
        and: "mock"
        defaultProperties.override(environment) >> {
            def properties = Mock(ConfigClientProperties)
            properties.getUsername() >> { return "userName" }
            properties.getPassword() >> { return password }
            properties.getAuthorization() >> { return authorization }
            properties.getLabel() >> { return "label1,label2" }
            properties.getHeaders() >> {
                def headers = new HashMap<String, String>()
                return headers
            }
            return properties
        }
        when: "方法调用"
        configServicePropertySourceLocator.locate(environment)
        then: "无异常抛出"
        noExceptionThrown()
        where: "分支覆盖"
        password   | authorization
        null       | "bearer 5a6c39ea-0619-419e-832f-a8e05222108c"
        "password" | null
    }

    def "Locate2"() {
        given: "方法参数准备"
        def environment = Mock(org.springframework.core.env.Environment)
        def restTemplate = Mock(RestTemplate)
        restTemplate.exchange(*_) >> {
            def entity = Mock(ResponseEntity)
            entity.getStatusCode() >> { return HttpStatus.OK }
            entity.getBody() >> {
                def result = new Environment(Mock(Environment))
                result.setName("name")
                result.getName()
                result.setLabel("label1,label2")
                result.getLabel()
                result.setVersion("version")
                result.setState("state")
                def profiles = new String[0]
                result.setProfiles(profiles)
                result.getProfiles()
                def source1 = new HashMap<String, Object>()
                source1.put("zuul.routes.test.path", "path1")
                source1.put("zuul.routes.test.id", "id1")
                source1.put("zuul.routes.test.stripPrefix", "stripPrefix1")
                source1.put("zuul.routes.test.retryable", true)
                source1.put("zuul.routes.test.url", "url1")
                def ps1 = new PropertySource("ps1", source1)
                def source2 = new HashMap<String, String>()
                source2.put("zuul.routes.test.path", "path2")
                def ps2 = new PropertySource("ps2", source2)
                def source3 = new HashMap<String, String>()
                source3.put("zuul.routes.test.path", "path3")
                source3.put("zuul.routes.test.serviceId", "serviceId1")
                def ps3 = new PropertySource("ps3", source3)
                def list = new ArrayList<PropertySource>()
                list.add(ps1)
                result.add(ps2)
                result.addFirst(ps3)
                result.addAll(list)
                return result
            }
            return entity
        }
        configServicePropertySourceLocator.setRestTemplate(restTemplate)
        and: "mock"
        defaultProperties.override(environment) >> {
            def properties = Mock(ConfigClientProperties)
            properties.getLabel() >> { return "label1,label2" }
            properties.getToken() >> { return "5a6c39ea-0619-419e-832f-a8e05222108c" }
            return properties
        }
        when: "方法调用"
        configServicePropertySourceLocator.locate(environment)
        then: "无异常抛出"
        noExceptionThrown()
    }


}
