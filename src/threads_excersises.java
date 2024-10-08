public class threads_excersises {

    private static int w=0;

    static class increasingThreads implements Runnable {

        private int numOfLoops;
        public increasingThreads(int numOfLoops){
            this.numOfLoops=numOfLoops;
        }
        @Override
        public void run() {
            for (int i=0;i<numOfLoops;i++){
                synchronized(threads_excersises.class){
                    w++;
                }
            }
        }
    }

    static class decreasingThreads implements Runnable {
        private int numOfLoops;
        public decreasingThreads(int numOfLoops){
            this.numOfLoops=numOfLoops;
        }
        @Override
        public void run() {
            for (int i=0;i<numOfLoops;i++){
                synchronized(threads_excersises.class){
                    w--;
                }
            }
        }
    }

    public static void runThreads(int numOfThreads, int numOfLoops) {
        Thread[] threads=new Thread[numOfThreads];
        for (int i=0;i<numOfThreads;i++){
            if (i%2==0){
                threads[i]=new Thread(new increasingThreads(numOfLoops));
            }
            else {
                threads[i]=new Thread(new decreasingThreads(numOfLoops));
            }
            threads[i].start();
        }

        for (int i=0;i<numOfThreads;i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(w);


    }
    public static void main(String[] args){
        int numOfOperations=2*(int)Math.pow(10,5);//for the tests it will be easier for it to be power of ten

        for (int i=2;i<=numOfOperations;i*=10){
            System.out.printf("Threads:%d   Loops:%d    Value:",i,numOfOperations/i);
            runThreads(i,numOfOperations/i);
            w=0;
        }
    }
}