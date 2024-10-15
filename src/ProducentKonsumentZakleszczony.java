import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BuforMonitor {
    private int maxSize; // Maksymalna pojemność bufora
    private int currentSize = 0; // Aktualna liczba elementów w buforze
    private final int M; // Maksymalna liczba elementów w jednej operacji
    private final Lock lock = new ReentrantLock(); // Lock do synchronizacji
    private final Condition notFull = lock.newCondition(); // Warunek: bufor nie jest pełny
    private final Condition notEmpty = lock.newCondition(); // Warunek: bufor nie jest pusty

    public BuforMonitor(int M) {
        this.M = M;
        //this.maxSize = 2 * M; // Bufor o pojemności 2 * M
        this.maxSize = 1; // Bufor ma pojemność 1 (minimalna pojemność)
    }

    // Producent dodaje losową liczbę elementów do bufora (nie więcej niż M)
    public void produkuj(int id) throws InterruptedException {
        int ile = 1; // Producent produkuje 1 element
        lock.lock(); // Zablokuj dostęp do bufora
        try {
            while (currentSize + ile > maxSize) {
                System.out.println("Producent " + id + " czeka. Zbyt mało miejsca w buforze.");
                notFull.await(); // Czekaj, aż w buforze będzie miejsce
            }

            // Kopiowanie elementów do bufora (symulacja)
            currentSize += ile;
            System.out.println("Producent " + id + " dodał " + ile + " elementów. Bufor: " + currentSize + "/" + maxSize);

            notEmpty.signalAll(); // Powiadom konsumentów, że są nowe elementy
        } finally {
            lock.unlock(); // Odblokuj dostęp do bufora
        }
    }

    // Konsument pobiera losową liczbę elementów z bufora (nie więcej niż M)
    public void konsumuj(int id) throws InterruptedException {
        int ile = 1; // Konsument pobiera 1 element
        lock.lock(); // Zablokuj dostęp do bufora
        try {
            while (currentSize < ile) {
                System.out.println("Konsument " + id + " czeka. Zbyt mało elementów w buforze.");
                notEmpty.await(); // Czekaj, aż w buforze będą elementy
            }

            // Kopiowanie elementów z bufora (symulacja)
            currentSize -= ile;
            System.out.println("Konsument " + id + " pobrał " + ile + " elementów. Bufor: " + currentSize + "/" + maxSize);

            notFull.signalAll(); // Powiadom producentów, że jest miejsce na nowe elementy
        } finally {
            lock.unlock(); // Odblokuj dostęp do bufora
        }
    }
}

// Producent
class Producent extends Thread {
    private BuforMonitor monitor;
    private int id;

    public Producent(BuforMonitor monitor, int id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                monitor.produkuj(id); // Producent próbuje dodać elementy do bufora
                Thread.sleep(1000); // Symulacja czasu produkcji
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Konsument
class Konsument extends Thread {
    private BuforMonitor monitor;
    private int id;

    public Konsument(BuforMonitor monitor, int id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                monitor.konsumuj(id); // Konsument próbuje pobrać elementy z bufora
                Thread.sleep(1000); // Symulacja czasu konsumpcji
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Klasa testowa
public class ProducentKonsumentZakleszczony {
    public static void main(String[] args) {
        int M = 10; // Maksymalna liczba elementów, które mogą być dodane/pobrane jednocześnie
        BuforMonitor monitor = new BuforMonitor(M);

        // Tworzenie producentów i konsumentów
        new Producent(monitor, 1).start(); // Start jednego producenta
        new Konsument(monitor, 1).start(); // Start pierwszego konsumenta
        new Konsument(monitor, 2).start(); // Start drugiego konsumenta
    }
}

