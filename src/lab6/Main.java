package lab6;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int maxPortion = 5;
        int bufferSize = 2*maxPortion;
        int testDurationSeconds = 5;
        int numProducers=5;
        int numConsumers= 5;
        

        //System.out.println("Testing TwoConditions:");
        //operationsIn5s(new TwoConditions(bufferSize), testDurationSeconds, maxPortion, numProducers ,numConsumers);
        /*for(int i=1;i<=5;i++){
            maxPortion=5*i;
            bufferSize=2*maxPortion;    
            //System.out.println("Testing ThreeLocks 12 threads with buffer size = "+bufferSize);
            operationsIn5s(new ThreeLocks(bufferSize), testDurationSeconds, maxPortion, numProducers ,numConsumers);
        }*/

        for(int o=0;o<5;o++){
            System.out.println("test " +o+": three locks");
        for(int i=1;i<=5;i++){
            numConsumers=5*i;
            numProducers = numConsumers;    
            //System.out.println("Testing ThreeLocks 12 threads with buffer size = "+bufferSize);
            operationsIn5s(new ThreeLocks(bufferSize), testDurationSeconds, maxPortion, numProducers ,numConsumers);
        }System.out.println("test " +o+": four conditions");
        for(int i=1;i<=5;i++){
            numConsumers=5*i;
            numProducers = numConsumers;    
            //System.out.println("Testing ThreeLocks 12 threads with buffer size = "+bufferSize);
            operationsIn5s(new FourConditions(bufferSize), testDurationSeconds, maxPortion, numProducers ,numConsumers);
        }
    }
        //System.out.println("Testing ThreeLocks:");
        //operationsIn5s(new ThreeLocks(bufferSize), testDurationSeconds, maxPortion, numProducers ,numConsumers);

    }

    private static void operationsIn5s(Monitor monitor, int durationSeconds, int maxPortion, int numProducers, int numConsumers)
            throws InterruptedException {
        AtomicInteger totalOperations = new AtomicInteger(0);
        Random random = new Random();

        

        AtomicInteger[] producerOperations = new AtomicInteger[numProducers];
        AtomicInteger[] consumerOperations = new AtomicInteger[numConsumers];

        for (int i = 0; i < numProducers; i++) {
            producerOperations[i] = new AtomicInteger(0);
        }
        for (int i = 0; i < numConsumers; i++) {
            consumerOperations[i] = new AtomicInteger(0);
        }

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        // Tworzymy producentów
        for (int i = 0; i < numProducers; i++) {
            int index = i; // Utrwalamy indeks do użycia w wyrażeniu lambda
            producers[i] = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        int randomAddAmount = 1 + random.nextInt( maxPortion ); // Losowa liczba z przedziału 1 -
                                                                                  // (bufferSize / 2)
                        monitor.Add(randomAddAmount);
                        producerOperations[index].incrementAndGet();
                        totalOperations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Tworzymy konsumentów
        for (int i = 0; i < numConsumers; i++) {
            int index = i; // Utrwalamy indeks do użycia w wyrażeniu lambda
            consumers[i] = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        int randomSubtractAmount = 1 + random.nextInt(maxPortion); // Losowa liczba z przedziału 1 -
                                                                                       // (bufferSize / 2)
                        monitor.Sub(randomSubtractAmount);
                        consumerOperations[index].incrementAndGet();
                        totalOperations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Uruchamiamy producentów i konsumentów
        for (Thread producer : producers) {
            producer.start();
        }
        for (Thread consumer : consumers) {
            consumer.start();
        }

        // Pozwalamy na działanie przez ustalony czas
        Thread.sleep(durationSeconds * 1000);

        // Przerywamy wątki
        for (Thread producer : producers) {
            producer.interrupt();
        }
        for (Thread consumer : consumers) {
            consumer.interrupt();
        }

        // Czekamy, aż wszystkie wątki się zakończą
        for (Thread producer : producers) {
            producer.join();
        }
        for (Thread consumer : consumers) {
            consumer.join();
        }

        // Wyświetlamy wyniki dla danego rozwiązania
        //System.out.println("Total operations: " + (totalOperations.get()));

        // Wywołujemy metodę, która wypisze liczbę operacji dla każdego wątku
        //operationsForEveryThread(producerOperations, consumerOperations);
        System.out.println((int)calculateStandardDeviation(consumerOperations, producerOperations));
        //System.out.println();
    }

    private static void operationsForEveryThread(AtomicInteger[] producerOperations,
            AtomicInteger[] consumerOperations) {

        for (int i = 0; i < producerOperations.length; i++) {
            System.out.println(producerOperations[i].get());
        }

        for (int i = 0; i < consumerOperations.length; i++) {
            System.out.println(+consumerOperations[i].get());
        } 
    }


    public static double calculateStandardDeviation(AtomicInteger[] dataP,AtomicInteger[] dataC) {
        double sum = 0.0;
        double mean;
        double variance = 0.0;
        
        // Krok 1: Oblicz średnią
        for (AtomicInteger num : dataP) {
            sum += num.doubleValue();
        }
        for (AtomicInteger num : dataC) {
            sum += num.doubleValue();
        }
        mean = sum / (dataP.length + dataC.length);

        // Krok 2: Oblicz wariancję
        for (AtomicInteger num : dataC) {
            variance += Math.pow(num.doubleValue() - mean, 2);
        }
        for (AtomicInteger num : dataP) {
            variance += Math.pow(num.doubleValue() - mean, 2);
        }
        variance = variance / (dataP.length + dataC.length);

        // Krok 3: Pierwiastek kwadratowy z wariancji - odchylenie standardowe
        return Math.sqrt(variance);
    }
}
