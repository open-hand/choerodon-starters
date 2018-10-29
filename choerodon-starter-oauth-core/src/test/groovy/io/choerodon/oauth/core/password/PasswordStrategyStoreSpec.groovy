package io.choerodon.oauth.core.password

import io.choerodon.oauth.core.password.validator.password.RegularStrategy
import org.springframework.context.ApplicationContext
import spock.lang.Specification

class PasswordStrategyStoreSpec extends Specification {

    def "test get Provider"() {
        given: 'mock ApplicationContext'
        def context = Mock(ApplicationContext) {
            def map = new HashMap<String, PasswordStrategy>()
            map.put(PasswordPolicyType.REGULAR.getValue(), new RegularStrategy())
            getBeansOfType(PasswordStrategy) >> map
        }
        def store = new PasswordStrategyStore(context)

        when:
        store.init()
        def map = store.getStrategyMap()

        then:
        map != null

        when:
        def strategy = store.getProvider(PasswordPolicyType.REGULAR.getValue())

        then:
        strategy != null
    }
}