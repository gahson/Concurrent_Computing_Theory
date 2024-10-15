import java.util.Random;

class BuforMonitor {
    private int maxSize; // Maksymalna pojemność bufora
    private int currentSize = 0; // Aktualna liczba elementów w buforze
    private final int M; // Maksymalna liczba elementów w jednej operacji
    private Random rand = new Random();

    public BuforMonitor(int M) {
        this.M = M;
        this.maxSize = 2 * M; // Bufor o pojemności 2 * M
    }

    // Producent dodaje losową liczbę elementów do bufora (nie więcej niż M)
    public synchronized void produkuj(int id) throws InterruptedException {
        int ile = rand.nextInt(M) + 1; // Producent produkuje losową liczbę elementów (1-M)
        while (currentSize + ile > maxSize) {
            System.out.println("Producent " + id + " czeka. Zbyt mało miejsca w buforze.");
            wait(); // Czekaj, jeśli w buforze nie ma miejsca na wszystkie elementy
        }

        // Kopiowanie elementów do bufora (symulacja)
        currentSize += ile;
        System.out.println("Producent " + id + " dodał " + ile + " elementów. Bufor: " + currentSize + "/" + maxSize);

        notifyAll(); // Powiadom konsumentów, że są nowe elementy
    }

    // Konsument pobiera losową liczbę elementów z bufora (nie więcej niż M)
    public synchronized void konsumuj(int id) throws InterruptedException {
        int ile = rand.nextInt(M) + 1; // Konsument pobiera losową liczbę elementów (1-M)
        while (currentSize < ile) {
            System.out.println("Konsument " + id + " czeka. Zbyt mało elementów w buforze.");
            wait(); // Czekaj, jeśli nie ma wystarczającej liczby elementów w buforze
        }

        // Kopiowanie elementów z bufora (symulacja)
        currentSize -= ile;
        System.out.println("Konsument " + id + " pobrał " + ile + " elementów. Bufor: " + currentSize + "/" + maxSize);

        notifyAll(); // Powiadom producentów, że jest miejsce na nowe elementy
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
public class ProducentKonsumentRandom {
    public static void main(String[] args) {
        int M = 10; // Maksymalna liczba elementów, które mogą być dodane/pobrane jednocześnie
        BuforMonitor monitor = new BuforMonitor(M);

        // Tworzenie producentów i konsumentów
        for (int i = 1; i <= 3; i++) {
            new Producent(monitor, i).start(); // Start trzech producentów
        }
        for (int i = 1; i <= 2; i++) {
            new Konsument(monitor, i).start(); // Start dwóch konsumentów
        }
    }
}
