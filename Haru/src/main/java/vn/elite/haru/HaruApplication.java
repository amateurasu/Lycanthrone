package vn.elite.haru;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.boot.SpringApplication.run;

@RestController
@SpringBootApplication
public class HaruApplication {
    public static void main(String[] args) {
        run(HaruApplication.class, args);
    }

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
