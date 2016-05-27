package org.penny.flow;

import java.util.concurrent.TimeUnit;

public interface FlowController {
    void acquire() throws InterruptedException;

    boolean acquire(long timeout, TimeUnit timeUnit) throws InterruptedException;

    void acquire(int n) throws InterruptedException;

    boolean acquire(int n, long timeout, TimeUnit timeUnit)
            throws InterruptedException;

    void release();

    void release(int n);

    void releaseAll();
}
