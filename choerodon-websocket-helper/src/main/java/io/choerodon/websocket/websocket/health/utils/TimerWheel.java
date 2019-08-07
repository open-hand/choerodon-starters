package io.choerodon.websocket.websocket.health.utils;

import io.choerodon.websocket.tool.ThreadTool;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Time wheel algorithm implementation.
 * <p>
 * All time is in milliseconds.
 * The default partition is 512 slots, with each slot time interval of 100 milliseconds.
 * <p>
 * Note: the implementation is not a precise implementation. For example, if the setting fails after 1000ms, it may not be notified until 1100ms.
 * this is due to the nature of the algorithm.
 *
 * @author dongbin
 * @version 1.0 2019-07-26 14:04
 * @since 1.5
 */
public class TimerWheel<T> {

    private final static int DEFAULT_SLOT_NUMBER = 512;
    private final static int DEFAULT_DURATION = 100;
    private final Lock lock = new ReentrantLock();
    private final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private final TimeoutNotification<T> notification;
    private final List<Slot> wheel;
    private final ExecutorService worker;
    private final Map<T, Integer> removeHelp;
    private final long duration;
    private final int slotNumber;

    private volatile int currentSlot;

    /**
     * Take the default 512 slots, with an interval of 100 milliseconds, and fail without notice to construct a new time wheel.
     */
    public TimerWheel() {
        this(-1, -1, null);
    }

    /**
     * Failure is notified at a default interval of 512 slots and 100 millisecond
     *
     * @param notification Failure notifier.
     */
    public TimerWheel(TimeoutNotification<T> notification) {
        this(-1, -1, notification);
    }

    /**
     * Specifies the number of slots, the number of milliseconds between, and no notification of timeout.
     *
     * @param slotNumber Slot number.
     * @param duration Slot intervals are milliseconds.
     */
    public TimerWheel(int slotNumber, long duration) {
        this(slotNumber, duration, null);
    }

    /**
     * Specifies the number of slots, the number of milliseconds between, and notifies failure.
     *
     * @param slotNumber   slot number.
     * @param duration     Slot intervals are milliseconds.
     * @param notification Failure notifier.
     */
    public TimerWheel(int slotNumber, long duration, TimeoutNotification<T> notification) {

        if (duration <= 0) {
            this.duration = DEFAULT_DURATION;
        } else {
            this.duration = duration;
        }

        if (slotNumber <= 3) {
            this.slotNumber = DEFAULT_SLOT_NUMBER;
        } else {
            this.slotNumber = slotNumber;
        }

        if (notification == null) {
            this.notification = t -> 0;
        } else {
            this.notification = notification;
        }

        this.currentSlot = 0;

        wheel = new ArrayList<>(this.slotNumber);

        for (int i = 0; i < this.slotNumber; i++) {
            wheel.add(new Slot());
        }

        // Background threads are used, so no active shutdown is required.
        worker = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1),
            ThreadTool.buildNameThreadFactory("time-wheel", true));

        worker.submit(new PointTask());

        removeHelp = new HashMap();
    }

    public void stop() {
        worker.shutdownNow();
    }

    /**
     * Adds an object that expires at a specified time.
     *
     * @param target  Expired objects are required.
     * @param timeout Duration of survival.
     */
    public void add(T target, long timeout) {
        if (target == null) {
            throw new NullPointerException("Target object is null!");
        }
        if (timeout <= 0) {
            return;
        }
        long specialTimeout = timeout;
        if (specialTimeout < this.duration) {
            specialTimeout = this.duration;
        }
        int virtualSlotIndex, actuallySlotIndex, round;
        lock.lock();
        try {
            virtualSlotIndex = calculateVirtualSlot(specialTimeout);
            actuallySlotIndex = calculateActuallySlot(virtualSlotIndex);
            round = calculateRuound(virtualSlotIndex);

            Slot slot = wheel.get(actuallySlotIndex);
            slot.add(target, round);


            removeHelp.put(target, actuallySlotIndex);

        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds a target that expires at a specified time.
     *
     * @param target     Target instance.
     * @param expireDate Due time.
     */
    public void add(T target, Date expireDate) {
        add(target, expireDate.getTime() - System.currentTimeMillis());
    }

    /**
     * Deletes the target from the ring. No expiration callback is triggered.
     *
     * @param target instance.
     */
    public void remove(T target) {
        lock.lock();
        try {

            doRemove(target);

        } finally {
            lock.unlock();
        }
    }


    /**
     * A total of expired targets are held in the current ring.
     *
     * @return Total number of unexpired targets.
     */
    public int size() {
        lock.lock();
        try {
            return removeHelp.size();
        } finally {
            lock.unlock();
        }
    }

    private void doRemove(T target) {
        if (target == null) {
            return;
        }


        Integer slotIndex = this.removeHelp.remove(target);
        if (slotIndex != null) {
            Slot slot = this.wheel.get(slotIndex);
            slot.remove(target);
        }

    }

    /**
     * Calculate the round
     */
    private int calculateRuound(int virtualSlot) {
        return virtualSlot / slotNumber;
    }

    private int calculateActuallySlot(int virtualSlot) {
        return virtualSlot % slotNumber;
    }

    /**
     * Computes virtual slot. Exceeds maximum slot bit.
     */
    private int calculateVirtualSlot(long timeout) {
        return (int) (currentSlot + timeout / duration);
    }

    /**
     * Slot implementation.
     */
    private class Slot {

        private final List<Element> elements = new LinkedList<>();

        public void add(T obj, int round) {

            Element element = new Element(obj, round);
            elements.add(element);

        }

        public List<T> expire() {
            List<T> expireList = new LinkedList<>();
            Iterator<Element> iter = elements.iterator();
            Element element;
            while (iter.hasNext()) {
                element = iter.next();
                if (element.getRound() <= 0) {
                    expireList.add(element.getTarget());
                    iter.remove();
                } else {
                    element.reduceRound();
                }
            }

            return expireList;
        }

        public void remove(Object target) {
            if (elements.isEmpty()) {
                return;
            }

            int index = 0;
            for (; index < elements.size(); index++) {
                if (elements.get(index).getTarget().equals(target)) {
                    break;
                }
            }
            elements.remove(index);
        }

        @Override
        public String toString() {
            return "Slot{" + "elements=" + elements + '}';
        }

    }

    private class Element {

        private final T target;
        private int round;

        public Element(T target, int round) {
            this.target = target;
            this.round = round;
        }

        public T getTarget() {
            return target;
        }

        public int reduceRound() {
            return round--;
        }

        public int getRound() {
            return round;
        }

        @Override
        public String toString() {
            return "Element{" + "target=" + target + ", round=" + round + '}';
        }

    }

    private class PointTask implements Runnable {

        @Override
        public void run() {

            List<T> expireList;
            Slot slot;
            do {

                lock.lock();
                try {
                    slot = wheel.get(currentSlot);
                    expireList = slot.expire();

                    currentSlot = (currentSlot + 1) % slotNumber;
                } finally {
                    lock.unlock();
                }

                long resultTime;
                try {
                    for (T target : expireList) {
                        resultTime = notification.notice(target);

                        doRemove(target);

                        if (resultTime > 0) {
                            add(target, resultTime);
                        }
                    }
                } catch (Throwable ex) {

                    // No exceptions are handled, just in case the thread unexpectedly terminates.
                    ex.printStackTrace(System.err);
                }

                try {
                    timeUnit.sleep(duration);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            } while (!Thread.interrupted());
        }

    }

}
