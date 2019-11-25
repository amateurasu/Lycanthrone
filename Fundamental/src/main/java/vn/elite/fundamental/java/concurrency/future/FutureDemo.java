package vn.elite.fundamental.java.concurrency.future;

import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureDemo {
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Random random = new Random();
    private static int bound = 10;

    public static void main(String[] args) {
        val start = System.currentTimeMillis();
        val list = runConcurrency();
        val result = getResult(list);

        long b4shutdown = System.currentTimeMillis();
        System.out.println("Before shutdown: " + (b4shutdown - start));
        executor.shutdown();
        while (!executor.isShutdown()) {}
        System.out.println("Shutdown time: " + (System.currentTimeMillis() - b4shutdown));

        result.forEach(System.out::println);
        System.out.println("ALL: " + (System.currentTimeMillis() - start));
    }

    private static Future<String> calculate(Integer input) {
        return executor.submit(() -> {
            System.out.printf(" %d ", input);

            if (input == random.nextInt(3) + 3) { // stimulating random fail in [3; 6)
                System.out.println("FAIL AT " + input);
                throw new Exception();
            }
            Thread.sleep(1000); // stimulating long task

            return input + " -> " + input * input;
        });
    }

    private static List<Future<String>> runConcurrency() {
        List<Future<String>> list = new ArrayList<>();

        for (int i = 0; i < bound; i++) {
            Future<String> future = calculate(i);
            list.add(future);
        }
        return list;
    }

    private static List<String> getResult(List<Future<String>> list) {
        List<String> result = new ArrayList<>();
        long start = System.currentTimeMillis();
        list.forEach(f -> {
            try {
                while (!f.isDone()) {
                    // Waiting
                }

                result.add(f.get());
            } catch (InterruptedException | ExecutionException ignored) { }
        });
        System.out.println("\nGetting result time: " + (System.currentTimeMillis() - start));
        return result;
    }
}
