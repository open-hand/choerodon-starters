package io.choerodon.metric.thread;

public interface ThreadStateBean {

    public int getThreadStatusNEWCount();

    public int getThreadStatusRUNNABLECount();

    public int getThreadStatusBLOCKEDCount();

    public int getThreadStatusWAITINGCount();

    public int getThreadStatusTIMEDWAITINGCount();

    public int getThreadStatusTERMINATEDCount();
}
