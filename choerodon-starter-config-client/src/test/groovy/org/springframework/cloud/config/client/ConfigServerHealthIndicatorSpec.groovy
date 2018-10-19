package org.springframework.cloud.config.client

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Health.Builder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.IntegrationTestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.core.env.CompositePropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertySource
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ConfigServerHealthIndicatorSpec extends Specification {

    def "DoHealthCheck"() {
        given: "准备方法参数"
        def builder = Mock(Builder)
        builder.unknown() >> { return new Health.Builder() }
        and: "准备构造器参数"
        def properties = new ConfigClientHealthProperties()
        def environment = Mock(Environment)
        environment.getActiveProfiles() >> {
            def strings = new String[1]
            return strings
        }
        def locator = Mock(ConfigServicePropertySourceLocator)
        locator.locate(environment) >> {
            def propertySource = Mock(CompositePropertySource)
            propertySource.getPropertySources() >> {
                def sources = new ArrayList<PropertySource>()
                def ps = Mock(CompositePropertySource)
                ps.getName() >> { return "name" }
                sources.add(ps)
                return sources
            }
            return propertySource
        }
        and: "构造ConfigServerHealthIndicator"
        ConfigServerHealthIndicator configServerHealthIndicator = new ConfigServerHealthIndicator(locator, environment, properties)
        when: "方法调用"
        configServerHealthIndicator.doHealthCheck(builder)
        then: "结果分析"
        noExceptionThrown()
    }
}
