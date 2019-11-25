package vn.elite.fundamental.java.concurrency;

import lombok.val;

import java.util.LinkedList;

public class ProducerConsumer {
    private final int CAPACITY = 2;
    // This class has a list, producer (adds items to list and consumer (removes items).
    // Create a list shared by producer and consumer
    // Size of list is 2.
    private LinkedList<Integer> list = new LinkedList<>();

    public static void main(String[] args) throws InterruptedException {
        // Object of a class that has both produce()
        // and consume() methods
        val pc = new ProducerConsumer();

        // Create producer thread
        val t1 = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Create consumer thread
        val t2 = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Start both threads
        t1.start();
        t2.start();

        // t1 finishes before t2
        t1.join();
        t2.join();
    }

    // Function called by producer thread
    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (this) {
                // producer thread waits while list is full
                while (list.size() == CAPACITY) {
                    wait();
                }

                System.out.println("Producer produced-" + value);
                // to insert the jobs in the list
                list.add(value++);

                // notifies the consumer thread that now it can start consuming
                notify();
                // makes the working of program easier to understand
                Thread.sleep(1000);
            }
        }
    }

    // Function called by consumer thread
    public void consume() throws InterruptedException {
        while (true) {
            synchronized (this) {
                // consumer thread waits while list is empty
                while (list.size() == 0) {
                    wait();
                }
                //to retrieve the first job in the list
                int val = list.removeFirst();
                System.out.println("Consumer consumed-" + val);
                // Wake up producer thread
                notify();
                // and sleep
                Thread.sleep(1000);
            }
        }
    }
}
