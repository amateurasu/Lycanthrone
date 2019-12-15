package vn.elite.haru;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;

@EnableScheduling
@SpringBootApplication
public class HaruApplication {
    public static void main(String[] args) {
        run(HaruApplication.class, args);
    }
}
