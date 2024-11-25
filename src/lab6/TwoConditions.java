package lab6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class TwoConditions implements Monitor {

    private int in_buf;
    private final int bufferSize;
    ReentrantLock lock = new ReentrantLock();

    Condition producerCond = lock.newCondition();
    Condition consumerCond = lock.newCondition();

    public TwoConditions(int bufferSize) {
        this.bufferSize = bufferSize;
        this.in_buf = 0;
    }

    public void Add(int howMany) throws InterruptedException {
        lock.lock();
        try {
            while (in_buf + howMany > bufferSize) {
                producerCond.await();
            }

            in_buf += howMany;

            consumerCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public void Sub(int howMany) throws InterruptedException {
        lock.lock();
        try {
            while (in_buf - howMany < 0) {
                consumerCond.await();
            }

            in_buf -= howMany;

            producerCond.signal();
        } finally {
            lock.unlock();
        }
    }

    public void Get() throws InterruptedException {
        lock.lock();
        System.out.println("Queue: " + in_buf);
        lock.unlock();
    }

}