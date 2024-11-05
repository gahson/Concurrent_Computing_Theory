package excersises;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bufor {
    private final int M;
    
    private int jest; // Liczba elementów w buforze
    private final ReentrantLock lock = new ReentrantLock(); // Monitor
    private final Condition PIERWSZYPROD = lock.newCondition();
    private final Condition RESZTAPROD = lock.newCondition();
    private final Condition PIERWSZYKONS = lock.newCondition();
    private final Condition RESZTAKONS = lock.newCondition();

    private boolean czyPierwszyProdCzeka = false;
    private boolean czyPierwszyKonsCzeka = false;

    public Bufor(int M) {
        this.M = M;
        
        this.jest = 0;
    }

    // Metoda WSTAW - wstawienie porcji do bufora
    public void WSTAW(int ile) throws InterruptedException {
        lock.lock();
        try {
            while (czyPierwszyProdCzeka) {
                RESZTAPROD.await(); // Inny producent czeka
            }
            czyPierwszyProdCzeka = true;
            while (2 * M - jest < ile) {
                PIERWSZYPROD.await(); // Czekanie na "ile" wolnych miejsc
            }
            // Wstawienie elementów do bufora
            
            jest += ile;

            // Powiadomienie innych czekających producentów
            RESZTAPROD.signal();
            // Powiadomienie pierwszego konsumenta
            PIERWSZYKONS.signal();
            czyPierwszyProdCzeka = false;
        } finally {
            lock.unlock();
        }
    }

    // Metoda POBIERZ - pobranie porcji z bufora
    public void POBIERZ(int ile) throws InterruptedException {
        lock.lock();
        try {
            while (czyPierwszyKonsCzeka) {
                RESZTAKONS.await(); // Inny konsument czeka
            }
            czyPierwszyKonsCzeka = true;
            while (jest < ile) {
                PIERWSZYKONS.await(); // Czekanie na "ile" elementów w buforze
            }
            
            jest -= ile;

            // Powiadomienie innych czekających konsumentów
            RESZTAKONS.signal();
            // Powiadomienie pierwszego producenta
            PIERWSZYPROD.signal();
            czyPierwszyKonsCzeka = false;
        } finally {
            lock.unlock();
        }
    }
}

// Przykładowe klasy Producent i Konsument
class Producent extends Thread {
    private final Bufor bufor;
    private final int maxPortion;
    private final int id;

    public Producent(Bufor bufor, int maxPortion, int id) {
        this.bufor = bufor;
        this.maxPortion = maxPortion;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //int ile = (int) (Math.random() * maxPortion) + 1; // Losowa liczba porcji
                int ile = maxPortion;
                bufor.WSTAW(ile);
                System.out.println("Producent " + id + " wstawił " + ile + " elementów");
                Thread.sleep((int) (Math.random() * 1000)); // Symulacja pracy
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Konsument extends Thread {
    private final Bufor bufor;
    private final int maxPortion;
    private final int id;

    public Konsument(Bufor bufor, int maxPortion, int id) {
        this.bufor = bufor;
        this.maxPortion = maxPortion;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                //int ile = (int) (Math.random() * maxPortion) + 1; // Losowa liczba porcji
                int ile = maxPortion;
                
                bufor.POBIERZ(ile);
                System.out.println("Konsument " + id + " pobrał " + ile + " elementów");
                Thread.sleep((int) (Math.random() * 1000)); // Symulacja pracy
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Klasa główna do uruchomienia programu
public class Fourconditions {
    public static void main(String[] args) {
        int M = 10; // Rozmiar bufora
        Bufor bufor = new Bufor(M);

        // Tworzenie producentów i konsumentów
        Producent producent1 = new Producent(bufor, 1, 1);
        Konsument konsument1 = new Konsument(bufor, 1, 1);
        Konsument konsument2 = new Konsument(bufor, 5, 2);
        Konsument konsument3 = new Konsument(bufor, 2, 3);

        // Uruchamianie wątków
        producent1.start();
        konsument1.start();
        konsument2.start();
    }
}
