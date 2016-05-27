package org.penny.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 信号量的最大值是限定的，不能超过其容量
 */
public class MaximumLimitedSemaphore {
    private final Sync sync;
    private final int capacity;

    public MaximumLimitedSemaphore(int permits) {
        if (permits < 0)
            throw new IllegalArgumentException();
        capacity = permits;
        sync = new Sync(permits);
    }

    public void acquire() throws InterruptedException {
        this.acquire(1);
    }

    public boolean acquire(long timeout, TimeUnit timeUnit)
            throws InterruptedException {
        return this.acquire(1, timeout, timeUnit);
    }

    public void acquire(int permits) throws InterruptedException {
        if (permits < 0)
            throw new IllegalArgumentException();
        sync.acquireSharedInterruptibly(permits);
    }

    public boolean acquire(int permits, long timeout, TimeUnit timeUnit)
            throws InterruptedException {
        if (permits < 0)
            throw new IllegalArgumentException();

        return sync.tryAcquireSharedNanos(permits, timeUnit.toNanos(timeout));
    }

    public void release() {
        sync.releaseShared(1);
    }

    public void release(int permits) {
        if (permits < 0)
            throw new IllegalArgumentException();
        sync.releaseShared(permits);
    }

    public void releaseAll() {
        sync.releaseShared(capacity);
    }

    public int remaining() {
        return sync.getPermits();
    }

    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4599884845712663565L;
        private final int capacity;

        Sync(int permits) {
            capacity = permits;
            setState(permits);
        }

        final int getPermits() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            for (; ; ) {
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 || compareAndSetState(available, remaining))
                    return remaining;
            }
        }

        protected final boolean tryReleaseShared(int releases) {
            for (; ; ) {
                int current = getState();
                int next = current + releases;

                if (next < current) // overflow
                    throw new Error("Maximum permit count exceeded");

                // 如果释放的信号量超过容量，则设定为容量的大小
                if (next > capacity)
                    next = capacity;

                if (compareAndSetState(current, next))
                    return true;
            }
        }
    }
}
