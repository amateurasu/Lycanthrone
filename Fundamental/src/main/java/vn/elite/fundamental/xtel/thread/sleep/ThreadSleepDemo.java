package vn.elite.fundamental.xtel.thread.sleep;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class ThreadSleepDemo {

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(new Date());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.error("Error", ex);
                }
            }
        });
        t1.start();
    }
}
