public class threads_excersises {
    private static int w=0;
    private static final int NUM_OF_LOOPS=10;
    private static final int NUM_OF_THREADS=10;//needs to be even to have same amount od decreasing and increasing threads

    static class increasingThreads implements Runnable {


        @Override
        public void run() {
            for (int i=0;i<NUM_OF_LOOPS;i++){
                w++;
            }
        }
    }

    static class decreasingThreads implements Runnable {


        @Override
        public void run() {
            for (int i=0;i<NUM_OF_LOOPS;i++){
                w--;
            }
        }
    }

    public static void main(String[] args) {
        Thread[] threads=new Thread[NUM_OF_THREADS];
        for (int i=0;i<NUM_OF_THREADS;i++){
            if (i%2==0){
                threads[i]=new Thread(new increasingThreads());
            }
            else {
                threads[i]=new Thread(new decreasingThreads());
            }
            threads[i].start();
        }

        for (int i=0;i<NUM_OF_THREADS;i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(w);


    }
}