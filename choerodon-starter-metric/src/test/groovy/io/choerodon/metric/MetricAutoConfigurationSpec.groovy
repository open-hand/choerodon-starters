package io.choerodon.metric


import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import spock.lang.Specification

class MetricAutoConfigurationSpec extends Specification {
    MetricAutoConfiguration metricAutoConfiguration = new MetricAutoConfiguration()

    def "ClassLoaderMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.classLoaderMetrics()
        then: "结果分析"
        metrics instanceof ClassLoaderMetrics
    }

    def "JvmMemoryMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.jvmMemoryMetrics()
        then: "结果分析"
        metrics instanceof JvmMemoryMetrics
    }

    def "GcMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.gcMetrics()
        then: "结果分析"
        metrics instanceof JvmGcMetrics
    }

    def "ProcessorMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.processorMetrics()
        then: "结果分析"
        metrics instanceof ProcessorMetrics
    }

    def "JvmThreadMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.jvmThreadMetrics()
        then: "结果分析"
        metrics instanceof JvmThreadMetrics
    }

    def "ThreadMetrics"() {
        when: "方法调用"
        def metrics = metricAutoConfiguration.threadMetrics()
        then: "结果分析"
        metrics instanceof ThreadMetrics
    }
}
