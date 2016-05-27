package org.penny.flow;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.penny.concurrent.MaximumLimitedSemaphore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RateFlowController implements FlowController {
    private ScheduledExecutorService scheduledExecutorService;
    private MaximumLimitedSemaphore semaphore;

    public RateFlowController(int rate) {
        this(rate, TimeUnit.SECONDS);
    }

    public RateFlowController(int rate, TimeUnit timeunit) {
        if (rate <= 0)
            throw new IllegalArgumentException();

        semaphore = new MaximumLimitedSemaphore(rate);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Rate-FlowController-%d").build());
        scheduledExecutorService.scheduleAtFixedRate(new RateWorker(), 1, 1, timeunit);
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    @Override
    public boolean acquire(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return semaphore.acquire(timeout, timeUnit);
    }

    public void acquire(int n) throws InterruptedException {
        semaphore.acquire(n);
    }

    @Override
    public boolean acquire(int n, long timeout, TimeUnit timeUnit) throws InterruptedException {
        return semaphore.acquire(n, timeout, timeUnit);
    }

    @Override
    public void release() {

    }

    @Override
    public void release(int n) {

    }

    @Override
    public void releaseAll() {

    }

    public synchronized void shutdown() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }

    private class RateWorker implements Runnable {
        @Override
        public void run() {
            semaphore.releaseAll();
        }
    }
}
