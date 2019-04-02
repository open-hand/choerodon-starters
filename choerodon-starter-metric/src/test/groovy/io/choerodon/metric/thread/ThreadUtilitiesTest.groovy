package io.choerodon.metric.thread


import spock.lang.Specification

import java.lang.management.ThreadInfo

class ThreadUtilitiesTest extends Specification {
    def "ThreadUtilities"() {
        when: "方法调用"
        ThreadUtilities.getRootThreadGroup()
        ThreadUtilities.getAllThreadGroups()
        ThreadUtilities.getThreadGroup("name")
        ThreadUtilities.getAllThreads()
        def mock = Mock(ThreadGroup)
        mock.activeCount() >> { return 1 }
        mock.enumerate(_, _) >> { return 2 }
        ThreadUtilities.getGroupThreads(mock)
        ThreadUtilities.getGroupThreads("name")
        ThreadUtilities.getAllThreadsPrioritized()
        ThreadUtilities.getAllDaemonThreads()
        ThreadUtilities.getThread("name")
        ThreadUtilities.getThread(1L)
        def mock1 = Mock(ThreadInfo)
        mock1.getThreadId() >> { return 1L }
        ThreadUtilities.getThread(mock1)
        ThreadUtilities.getAllThreadInfos()
        ThreadUtilities.getThreadInfo("name")
        ThreadUtilities.getThreadInfo(1L)
        def mock2 = Mock(Thread)
        mock2.getId() >> { return 1L }
        ThreadUtilities.getThreadInfo(mock2)
        ThreadUtilities.getLockingThread(Mock(Object))
        ThreadUtilities.getBlockingThread(mock2)
        then: "无异常抛出"
        noExceptionThrown()
    }

    def "ThreadUtilities[Exception]"() {
        when: "getThreadGroup-Null name"
        ThreadUtilities.getThreadGroup(null)
        then: "无异常抛出"
        def e1 = thrown(NullPointerException)
        e1.message == "Null name"

        when: "getLockingThread-Null object"
        ThreadUtilities.getLockingThread(null)
        then: "无异常抛出"
        def e5 = thrown(NullPointerException)
        e5.message == "Null object"
    }
}
