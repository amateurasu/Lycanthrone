package vn.elite.fundamental.xtel.thread.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Student student = new Student();
        student.study();
        student.listenToMusic();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(student::study);
        executorService.execute(student::listenToMusic);
        executorService.shutdown();
    }
}
