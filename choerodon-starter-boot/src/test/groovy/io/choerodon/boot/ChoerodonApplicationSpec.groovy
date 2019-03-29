package io.choerodon.boot

import spock.lang.Specification

class ChoerodonApplicationSpec extends Specification {

    def "Application Boot" () {
        when:
        ChoerodonApplication.main([] as String[])
        then:
        noExceptionThrown()
    }
}
