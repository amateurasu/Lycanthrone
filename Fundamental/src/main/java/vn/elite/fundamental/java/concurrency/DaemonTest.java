package vn.elite.fundamental.java.concurrency;

public class DaemonTest {

    public static void main(String[] args) {
        System.out.println("==> Main Thread running..\n");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("All threads are down!!!")));

        new DaemonThread("Daemon").start();
        new NoneDaemonThread("Non-Daemon 1").start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n==> Main Thread ending\n");
    }

    static class DaemonThread extends Thread {
        DaemonThread(String name) {
            super(name);
            setDaemon(true);
        }

        @Override
        public void run() {
            int count = 0;

            while (true) {
                System.out.printf("+ Hello from %s (id = %d) -> %d%n", getName(), getId(), count++);
                try {
                    sleep(2000);
                    if (count > 3000) {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void finalize() {
            System.out.printf("\n==> %s ending\n", getName());
        }
    }

    static class NoneDaemonThread extends Thread {
        NoneDaemonThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            int i = 0;
            while (i < 10) {
                try {
                    System.out.printf("  - Hello from %s - %d%n", getName(), i++);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Ghi ra thông báo luồng này kết thúc.
            System.out.printf("\n==> %s ending\n", getName());
        }
    }
}
