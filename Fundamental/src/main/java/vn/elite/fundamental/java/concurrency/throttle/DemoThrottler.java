package vn.elite.fundamental.java.concurrency.throttle;

import lombok.val;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DemoThrottler {
    public static void main(String[] args) {
        val customQueue = new ArrayBlockingQueue<Runnable>(50);
        val customExecutor = new CustomThreadPoolExecutor(10, 20, 5000, TimeUnit.MILLISECONDS, customQueue);
        throttle(customExecutor, "custom");

        // val blockingQueue = new ArrayBlockingQueue<Runnable>(50);
        // val blockingExecutor = new BlockingThreadPoolExecutor(10, 20, 5000, TimeUnit.MILLISECONDS, blockingQueue);
        // throttle(blockingExecutor, "blocking");
    }

    private static void throttle(ThreadPoolExecutor executor, String name) {
        executor.setRejectedExecutionHandler((runnable, executor1) -> {
            System.out.println("DemoTask Rejected : " + ((DemoTask) runnable).getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Lets add another time : " + ((DemoTask) runnable).getName());
            executor1.execute(runnable);
        });

        // Let start all core threads initially
        executor.prestartAllCoreThreads();
        int threadCounter = 0;
        do {
            threadCounter++;
            // Adding threads one by one
            System.out.println("Adding DemoTask : " + threadCounter);
            executor.execute(new DemoTask(name + " " + threadCounter));
        } while (threadCounter != 500);
    }
}
