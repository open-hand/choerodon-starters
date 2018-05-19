package io.choerodon.event.consumer.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.event.consumer.annotation.EventListener;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author flyleft
 * 2018/4/11
 */
public class EventConsumer {

    public final Method method;
    public final Object object;
    public final EventListener eventListener;
    public final TypeReference payLoadType;

    private String key;

    public EventConsumer(Method method, Object object,
                         EventListener eventListener, TypeReference payLoadType) {
        this.method = method;
        this.object = object;
        this.eventListener = eventListener;
        this.payLoadType = payLoadType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventConsumer that = (EventConsumer) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(object, that.object) &&
                Objects.equals(eventListener, that.eventListener) &&
                Objects.equals(payLoadType, that.payLoadType) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(method, object, eventListener, payLoadType, key);
    }
}
