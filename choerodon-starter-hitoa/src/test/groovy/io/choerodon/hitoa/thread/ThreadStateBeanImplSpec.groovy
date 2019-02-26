package io.choerodon.hitoa.thread

import spock.lang.Specification

class ThreadStateBeanImplSpec extends Specification {
    ThreadStateBeanImpl threadStateBean = new ThreadStateBeanImpl()

    def "GetCount"() {
        when: "方法调用"
        def NEWCount = threadStateBean.getThreadStatusNEWCount()
        def RUNNABLECount = threadStateBean.getThreadStatusRUNNABLECount()
        def BLOCKEDCount = threadStateBean.getThreadStatusBLOCKEDCount()
        def WAITINGCount = threadStateBean.getThreadStatusWAITINGCount()
        def TIMEDWAITINGCount = threadStateBean.getThreadStatusTIMEDWAITINGCount()
        def TERMINATEDCount = threadStateBean.getThreadStatusTERMINATEDCount()
        then: "结果分析"
        noExceptionThrown()
        NEWCount == 0
        RUNNABLECount != 0
        BLOCKEDCount == 0
        WAITINGCount != 0
        // todo  TIMEDWAITINGCount == 2
        //  TIMEDWAITINGCount == 0
        TERMINATEDCount == 0
    }
}
