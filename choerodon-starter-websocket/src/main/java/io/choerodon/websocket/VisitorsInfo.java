package io.choerodon.websocket;

public class VisitorsInfo {

    public final Integer currentOnlines;
    public final Integer numberOfVisitorsToday;

    public VisitorsInfo(Integer currentOnlines, Integer numberOfVisitorsToday) {
        this.currentOnlines = currentOnlines;
        this.numberOfVisitorsToday = numberOfVisitorsToday;
    }
}
