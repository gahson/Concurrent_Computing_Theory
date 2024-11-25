package lab6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreeLocks implements Monitor {

    private int in_buf;
    private final int bufferSize;

    ReentrantLock lock1 = new ReentrantLock();
    ReentrantLock lock2 = new ReentrantLock();
    ReentrantLock lock3 = new ReentrantLock();

    Condition queueWait = lock3.newCondition();

    boolean didFirstProducerProduce = true;
    boolean didFirstConsumerConsume = true;

    public ThreeLocks(int bufferSize) {
        this.bufferSize = bufferSize;
        this.in_buf = 0;
    }

    public void Add(int howMany) throws InterruptedException {
        lock1.lock();
        lock3.lock();
        try {
            while (in_buf + howMany > bufferSize) {
                queueWait.await();
            }

            in_buf += howMany;

            queueWait.signal();
        } finally {
            lock3.unlock();
            lock1.unlock();
        }
    }

    public void Sub(int howMany) throws InterruptedException {
        lock2.lock();
        lock3.lock();
        try {
            while (in_buf - howMany < 0) {
                queueWait.await();
            }

            in_buf -= howMany;

            queueWait.signal();
        } finally {
            lock3.unlock();
            lock2.unlock();
        }
    }

    public void Get() throws InterruptedException {
        lock3.lock();
        System.out.println("Queue: " + in_buf);
        lock3.unlock();
    }

}
