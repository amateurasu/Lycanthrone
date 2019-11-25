package vn.elite.fundamental.xtel.thread.sync;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Processing {

    private static final int SORTING = 0;
    private static final int SEARCHING = 1;
    private static final int OUTPUTTING = 2;

    private int[] array;
    private int status = 0;

    public Processing(int[] array) {
        this.array = array;
    }

    public synchronized void sort() {
        while (status != SORTING) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Begin Sorting");
        Arrays.sort(array);
        status++;
        notifyAll();
        System.out.println(Arrays.toString(array));
        System.out.println("End Sorting\n");
    }

    public synchronized int search(int search) {
        while (status != SEARCHING) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Begin Searching");
        int start = 0;
        int end = array.length;

        while (start <= end) {
            int mid = (start + end) / 2;

            if (search == array[mid]) {
                System.out.println("Found at: " + mid + "! End Searching\n");
                status++;
                notify();
                return mid;
            } else if (search > array[mid]) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        status++;
        notifyAll();
        System.out.println("Not found! End Searching\n");
        return -1;
    }

    public synchronized void save(String fileName, int search) {
        while (status < OUTPUTTING) {
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        System.out.println("Begin Saving");
        try (FileWriter out = new FileWriter(fileName)) {
            out.write("Sorted: " + Arrays.toString(array) + "\nSearch position: " + search);
            System.out.println("Saved");
        } catch (IOException ex) {
            System.out.println("Cannot save file!");
        }
        System.out.println("End Saving");
    }
}
