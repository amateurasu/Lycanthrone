package vn.elite.haru;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import vn.elite.haru.storage.StorageProperties;
import vn.elite.haru.storage.StorageService;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class HaruApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaruApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return args -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
