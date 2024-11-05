import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProdCons2cond {

    private int count = 0; // Liczba elementów w buforze
    private final int maxSize; // Maksymalny rozmiar bufora
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition(); // Kolejka warunkowa dla producentów
    private final Condition notEmpty = lock.newCondition(); // Kolejka warunkowa dla konsumentów

    public ProdCons2cond(int maxSize) {
        this.maxSize = maxSize;
    }

    public void produce(int quantity, int id) throws InterruptedException {
        lock.lock();
        try {
            // Czekamy, jeśli liczba elementów + ilość do dodania przekracza maksymalny rozmiar bufora
            while (count + quantity > maxSize) {
                notFull.await();
            }
            // Dodajemy elementy do "bufora"
            count += quantity;
            System.out.println("Producent " + id + " " + "Produced " + quantity + " items, buffer count: " + count);
            notEmpty.signal(); // Powiadamiamy konsumentów, że bufor nie jest pusty
        } finally {
            lock.unlock();
        }
    }

    public void consume(int quantity, int id) throws InterruptedException {
        lock.lock();
        try {
            // Czekamy, jeśli liczba elementów jest mniejsza niż ilość do pobrania
            while (count < quantity) {
                notEmpty.await();
            }
            // Usuwamy elementy z "bufora"
            count -= quantity;
            System.out.println("Consumer " + id + " " + "Consumed " + quantity + " items, buffer count: " + count);
            notFull.signal(); // Powiadamiamy producentów, że bufor ma wolne miejsce
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ProdCons2cond buffer = new ProdCons2cond(10);
        

        // Producent
        new Thread(() -> {
            try {
                while(true) {
                    int quantity = 1; // Losowa wielkość porcji (1-3)
                    buffer.produce(quantity , 1);
                    Thread.sleep(100); // Czas między operacjami
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Konsument
        new Thread(() -> {
            try {
                while(true) {
                    int quantity = 1; // Losowa wielkość porcji (1-3)
                    buffer.consume(quantity , 1);
                    Thread.sleep(150); // Czas między operacjami
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                while(true) {
                    int quantity = 5; // Losowa wielkość porcji (1-3)
                    buffer.consume(quantity , 2);
                    Thread.sleep(150); // Czas między operacjami
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}