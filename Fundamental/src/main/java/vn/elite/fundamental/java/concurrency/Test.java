package vn.elite.fundamental.java.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        long start = System.currentTimeMillis();

        executor.execute(Test::task);
        executor.execute(Test::task);

        executor.shutdown();
        while (!executor.isTerminated()) {
            // wait
        }
        System.out.println("total " + (System.currentTimeMillis() - start));
    }

    private static void task() {
        final String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            try {
                System.out.printf("%s %d%n", threadName, i);
                Thread.sleep(100);
            } catch (InterruptedException ignored) { }
        }
        System.out.printf("%s %d%n", threadName, System.currentTimeMillis() - start);
    }
}
