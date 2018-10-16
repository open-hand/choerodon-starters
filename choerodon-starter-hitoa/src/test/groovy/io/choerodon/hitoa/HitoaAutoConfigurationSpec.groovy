package io.choerodon.hitoa

import io.choerodon.hitoa.metrics.ThreadMetrics
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import spock.lang.Specification

class HitoaAutoConfigurationSpec extends Specification {
    HitoaAutoConfiguration hitoaAutoConfiguration = new HitoaAutoConfiguration()

    def "ClassLoaderMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.classLoaderMetrics()
        then: "结果分析"
        metrics instanceof ClassLoaderMetrics
    }

    def "JvmMemoryMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.jvmMemoryMetrics()
        then: "结果分析"
        metrics instanceof JvmMemoryMetrics
    }

    def "GcMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.gcMetrics()
        then: "结果分析"
        metrics instanceof JvmGcMetrics
    }

    def "ProcessorMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.processorMetrics()
        then: "结果分析"
        metrics instanceof ProcessorMetrics
    }

    def "JvmThreadMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.jvmThreadMetrics()
        then: "结果分析"
        metrics instanceof JvmThreadMetrics
    }

    def "ThreadMetrics"() {
        when: "方法调用"
        def metrics = hitoaAutoConfiguration.threadMetrics()
        then: "结果分析"
        metrics instanceof ThreadMetrics
    }
}
