package vn.elite.fundamental.java.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WorkerThread implements Runnable {
    private String message;

    public WorkerThread(String s) {
        this.message = s;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " (Start) message = " + message);
        processMessage();//call processMessage method that sleeps the thread for 2 seconds
        System.out.println(Thread.currentThread().getName() + " (End)");//prints thread name
    }

    private void processMessage() {

    }
}

public class TestThreadPool {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            int j = i;
            executor.execute(() -> {
                try {
                    System.out.printf("%s (Start) message = %d%n", Thread.currentThread().getName(), j);
                    Thread.sleep(3000);
                    System.out.printf("%s (End) %d%n", Thread.currentThread().getName(), j);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });//calling execute method of ExecutorService
        }
        executor.shutdown();
        while (!executor.isTerminated()) { }
        System.out.println("Finished all threads");
        System.out.printf("TOTAL TIME: %d", System.currentTimeMillis() - start);
    }
}
