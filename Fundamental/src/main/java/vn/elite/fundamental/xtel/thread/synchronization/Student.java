package vn.elite.fundamental.xtel.thread.synchronization;

public class Student {
    public synchronized void study() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("I am studying");
        }
    }

    public synchronized void listenToMusic() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("I am listening to music");
        }
    }
}
