package io.choerodon.resource.permission

import io.choerodon.resource.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class PublicPermissionSpec extends Specification {
    def "Equals"() {
        given: "参数准备"
        def permission1 = new PublicPermission("path", HttpMethod.PUT)
        def permission2 = new PublicPermission("path", HttpMethod.PUT)
        when: "equal"
        def equals1 = permission1.equals(permission1)
        def equals2 = permission1.equals(null)
        def equals3 = permission1.equals(permission2)
        then: "result"
        equals1 == true
        equals2 == false
        equals3 == true

        when: "hashCode"
        def hashCode1 = permission1.hashCode()
        def hashCode2 = permission2.hashCode()
        then: "result"
        hashCode1 == hashCode2

    }
}
