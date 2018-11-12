package io.choerodon.hitoa.metrics

import io.micrometer.core.instrument.MeterRegistry
import spock.lang.Specification

class ThreadMetricsSpec extends Specification {
    ThreadMetrics threadMetrics = new ThreadMetrics()

    def "BindTo"() {
        when: "方法调用"
        threadMetrics.bindTo(Mock(MeterRegistry))
        then: "无异常抛出"
        noExceptionThrown()
    }
}
