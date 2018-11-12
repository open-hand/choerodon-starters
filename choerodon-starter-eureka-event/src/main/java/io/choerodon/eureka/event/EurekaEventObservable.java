package io.choerodon.eureka.event;

import java.util.Observable;

public class EurekaEventObservable extends Observable {

    public void sendEvent(EurekaEventPayload payload) {
        setChanged();
        notifyObservers(payload);
    }
}
