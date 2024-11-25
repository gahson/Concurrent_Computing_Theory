package lab6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class FourConditions implements Monitor {
    // private int counter = 0;

    // private final Queue<Integer> queue;
    private final int bufferSize;
    private int in_buf;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition RESTCONSUMERCOND = lock.newCondition();
    private final Condition RESTPRODUCERCOND = lock.newCondition();
    
    private final Condition FIRSTPRODUCERCOND = lock.newCondition();
    private final Condition FIRSTCONSUMERCOND = lock.newCondition();

    boolean isFirstProducerWaiting = false;
    boolean isFirstConsumerWaiting = false;

    public FourConditions(int bufferSize) {
        this.bufferSize = bufferSize;
        this.in_buf = 0;
    }

    public void Add(int howMany) throws InterruptedException {
        lock.lock();
        try {
            while (isFirstProducerWaiting) {
                RESTPRODUCERCOND.await();
            }

            isFirstProducerWaiting = true;

            while (in_buf + howMany > bufferSize) {

                FIRSTPRODUCERCOND.await();
            }

            in_buf += howMany;

            isFirstProducerWaiting = false;
            RESTPRODUCERCOND.signal();
            FIRSTCONSUMERCOND.signal();
            
        } finally {
            lock.unlock();
        }
    }

    public void Sub(int howMany) throws InterruptedException {
        lock.lock();
        try {
            while (isFirstConsumerWaiting) {
                RESTCONSUMERCOND.await();
            }
            isFirstConsumerWaiting = true;

            while (in_buf - howMany < 0) {

                FIRSTCONSUMERCOND.await();
            }

            in_buf -= howMany;

            isFirstConsumerWaiting = false;
            RESTCONSUMERCOND.signal();
            FIRSTPRODUCERCOND.signal();
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