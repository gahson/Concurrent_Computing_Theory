
class MyRunnable implements Runnable {
    private int threadId;

    public MyRunnable(int id) {
        this.threadId = id;
    }

    @Override
    public void run() {
        System.out.println("Wątek " + threadId + " działa");
    }
}

public class threads_excersises {
    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            MyRunnable myRunnable = new MyRunnable(i);
            Thread thread = new Thread(myRunnable);
            thread.start();
        }
    }
}