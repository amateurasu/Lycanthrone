package vn.elite.fundamental.java.concurrency;

import javax.swing.*;
import java.awt.*;

public class SwingWorkerDemo {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                WorkerDemo w = new WorkerDemo();
                w.execute();
                Thread.sleep(1000);
                try {
                    w.cancel(false);
                } catch (RuntimeException rte) {
                    rte.printStackTrace();
                }
                Thread.sleep(6000);
            } catch (InterruptedException ignored) {}
        });
    }

    public static class WorkerDemo extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            while (!isCancelled()) {
                System.out.println("A");
                Thread.sleep(500);
                System.out.println("B");
            }
            System.out.println("I'm still alive");
            return null;
        }

        @Override
        protected void done() {throw new RuntimeException("I want to produce a stack trace!");}
    }
}
