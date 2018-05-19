package io.choerodon.event.producer.execute;

import io.choerodon.core.exception.CommonException;

/**
 * @author flyleft
 * 2018/5/16
 */
public class EventStoreClientFallback implements EventStoreClient {

    @Override
    public String createEvent(EventRecord eventRecord) {
        throw new CommonException("error.event.create");
    }

    @Override
    public void preConfirmEvent(String uuid, String messages) {
        throw new CommonException("error.event.preConfirm");
    }

    @Override
    public void confirmEvent(String uuid) {
        throw new CommonException("error.event.confirm");
    }

    @Override
    public void cancelEvent(String uuid) {
        throw new CommonException("error.event.cancel");
    }

}
