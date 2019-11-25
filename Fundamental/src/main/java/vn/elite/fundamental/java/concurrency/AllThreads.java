package vn.elite.fundamental.java.concurrency;

import lombok.val;

public class AllThreads extends Thread {
    public static void main(String[] args) {
        val t1 = new AllThreads();
        t1.setName("thread 1");
        t1.start();
        val t2 = new Thread("thread 2");
        t2.start();

        val currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        val lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);

        for (int i = 0; i < noThreads; i++) {
            System.out.printf("Thread No: %d = %s%n", i, lstThreads[i].getName());
        }
    }
}

