package io.choerodon.eureka.event;

import java.util.LinkedList;
import java.util.ListIterator;

public class SynchronizedLinkedList<E> extends LinkedList<E> {

    private final transient LinkedList<E> list;

    private final transient Object mutex;

    SynchronizedLinkedList(LinkedList<E> list) {
        this.list = list;
        mutex = this;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        synchronized (mutex) {return list.equals(o);}
    }
    public int hashCode() {
        synchronized (mutex) {return list.hashCode();}
    }

    @Override
    public boolean add(E e) {
        synchronized (mutex) {
            return list.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized (mutex) {
            return list.remove(o);
        }
    }

    @Override
    public E getFirst() {
        synchronized (mutex) {
            return list.getFirst();
        }
    }

    @Override
    public E get(int index) {
        synchronized (mutex) {
            return list.get(index);
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    @Override
    public int size() {
        synchronized (mutex) {
            return list.size();
        }
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public E removeFirst() {
        synchronized (mutex) {
            return list.removeFirst();
        }
    }
}
