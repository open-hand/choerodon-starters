package io.choerodon.liquibase.addition

import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification

/**
 *
 * @author zmf
 * @since 2018-10-16
 *
 */
class ProfileMapSpec extends Specification {
    private ProfileMap profileMap

    void setup() {
        profileMap = new ProfileMap()
        MockEnvironment environment = new MockEnvironment()
        environment.setProperty("data.name", "mysql")
        environment.setProperty("spring.datasource.username", "root")
        environment.setProperty("addition.datasource.username", "root")
        environment.setProperty("data.username", "root")
        profileMap.setEnvironment(environment)
    }

    def "GetValue"() {
        when: ""
        def value = profileMap.getValue(username)

        then: ""
        value == expectValue
        where:
        username << [null, "", "data.name"]
        expectValue << [null, null, "mysql"]
    }

    def "GetAdditionValue"() {
        when:
        def result = profileMap.getAdditionValue(username)

        then:
        result == expectValue

        where:
        username << [null, "", "username"]
        expectValue << [null, null, "root"]
    }

    def "GetSpringValue"() {
        when:
        def result = profileMap.getSpringValue(username)

        then:
        result == expectValue

        where:
        username << [null, "", "username"]
        expectValue << [null, null, "root"]
    }

    def "GetDataValue"() {
        expect:
        profileMap.getDataValue(username) == expectValue

        where:
        username << [null, "", "username"]
        expectValue << [null, null, "root"]
    }
}