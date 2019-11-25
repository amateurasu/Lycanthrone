package vn.elite.fundamental.java.concurrency.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelAdder {
    private static final int range = 1000000;

    public static void main(String[] args) {
        ParallelAdder adder = new ParallelAdder();
        int pSum = adder.parallelSum();
        int sSum = adder.sequentialSum();
        System.out.println("Parallel vn.elite.fundamental.sum equals to Sequential vn.elite.fundamental.sum: " + (pSum == sSum));
    }

    private Integer parallelSum() {
        long t1 = System.currentTimeMillis();
        ExecutorService executor = Executors.newCachedThreadPool();

        List<Future<Integer>> list = new ArrayList<>();
        int count = 1;
        int prev = 0;
        for (int i = 0; i < range; i++) {
            if (count % 2 == 0) {
                Future<Integer> future = executor.submit(new CallableAdder(prev, i));
                list.add(future);
                count = 1;
                continue;
            }
            prev = i;
            count++;
        }
        int totsum = 0;
        for (Future<Integer> fut : list) {
            try {
                totsum = totsum + fut.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total Sum is " + totsum);
        System.out.println("Time taken by parallelSum " + (System.currentTimeMillis() - t1));
        return totsum;
    }

    private int sequentialSum() {
        long t1 = System.currentTimeMillis();
        Integer totsum = 0;
        for (int i = 0; i < range; i++) {
            totsum = new CallableAdder(totsum, i).call();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("sequentialSum Total Sum is " + totsum);
        System.out.println("Time taken by sequentialSum " + (t2 - t1));
        return totsum;
    }

    class CallableAdder implements Callable<Integer> {
        private Integer operand1;
        private Integer operand2;

        CallableAdder(Integer operand1, Integer operand2) {
            this.operand1 = operand1;
            this.operand2 = operand2;
        }

        @Override
        public Integer call() {
            // System.out.println(Thread.currentThread().getName() +
            //         " says : partial Sum for " + operand1 + " and " + operand2 + " is " + (operand1 + operand2));
            return operand1 + operand2;
        }
    }
}
