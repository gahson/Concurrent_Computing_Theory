package excersises;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FourConditions2 {

    // Buffer class to manage production and consumption
    static class Buffer {
        private final int M;
        private final Queue<Integer> buffer; // Buffer stores elements as Integer
        private int count; // Number of elements in the buffer
        private final ReentrantLock lock = new ReentrantLock(); // Monitor
        private final Condition FIRST_PRODUCER = lock.newCondition();
        private final Condition OTHER_PRODUCERS = lock.newCondition();
        private final Condition FIRST_CONSUMER = lock.newCondition();
        private final Condition OTHER_CONSUMERS = lock.newCondition();

        public Buffer(int M) {
            this.M = M;
            this.buffer = new LinkedList<>();
            this.count = 0;
        }

        // INSERT method - insert quantity into the buffer
        public void INSERT(int quantity) throws InterruptedException {
            lock.lock();
            try {
                // Check if there are any other producers waiting, and wait if needed
                if (lock.hasWaiters(FIRST_PRODUCER)) {
                    OTHER_PRODUCERS.await(); // Another producer is waiting
                }

                // Wait until there is enough space in the buffer for "quantity" elements
                while (2 * M - count < quantity) {
                    FIRST_PRODUCER.await(); // Waiting for "quantity" free spaces
                }

                // Inserting elements into the buffer
                for (int i = 0; i < quantity; i++) {
                    buffer.add(1); // Insert dummy value (e.g., 1) as placeholder
                }
                count += quantity;

                // Notify other waiting producers
                OTHER_PRODUCERS.signal();
                // Notify first consumer
                FIRST_CONSUMER.signal();
            } finally {
                lock.unlock();
            }
        }

        // REMOVE method - remove quantity from the buffer
        public void REMOVE(int quantity) throws InterruptedException {
            lock.lock();
            try {
                // Check if there are any other consumers waiting, and wait if needed
                if (lock.hasWaiters(FIRST_CONSUMER)) {
                    OTHER_CONSUMERS.await(); // Another consumer is waiting
                }

                // Wait until there is enough elements in the buffer to remove "quantity"
                while (count < quantity) {
                    FIRST_CONSUMER.await(); // Waiting for "quantity" elements in buffer
                }

                // Removing elements from the buffer
                for (int i = 0; i < quantity; i++) {
                    buffer.poll(); // Remove dummy value
                }
                count -= quantity;

                // Notify other waiting consumers
                OTHER_CONSUMERS.signal();
                // Notify first producer
                FIRST_PRODUCER.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    // Producer class
    static class Producer extends Thread {
        private final Buffer buffer;
        private final int maxPortion;
        private final int id;

        public Producer(Buffer buffer, int maxPortion, int id) {
            this.buffer = buffer;
            this.maxPortion = maxPortion;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int quantity = (int) (Math.random() * maxPortion) + 1; // Random portion size
                    buffer.INSERT(quantity);
                    System.out.println("Producer " + id + " inserted " + quantity + " elements");
                    Thread.sleep((int) (Math.random() * 1000)); // Simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Consumer class
    static class Consumer extends Thread {
        private final Buffer buffer;
        private final int maxPortion;
        private final int id;

        public Consumer(Buffer buffer, int maxPortion, int id) {
            this.buffer = buffer;
            this.maxPortion = maxPortion;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int quantity = (int) (Math.random() * maxPortion) + 1; // Random portion size
                    buffer.REMOVE(quantity);
                    System.out.println("Consumer " + id + " removed " + quantity + " elements");
                    Thread.sleep((int) (Math.random() * 1000)); // Simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Main class to run the program
    public static void main(String[] args) {
        int M = 10; // Buffer size
        Buffer buffer = new Buffer(M);

        // Creating producers and consumers
        Producer producer1 = new Producer(buffer, 1, 1);
        Consumer consumer1 = new Consumer(buffer, 1, 1);
        Consumer consumer2 = new Consumer(buffer, 5, 2);

        // Starting threads
        producer1.start();
        consumer1.start();
        consumer2.start();
    }
}
