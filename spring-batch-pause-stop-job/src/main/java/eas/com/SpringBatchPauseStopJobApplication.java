package eas.com;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = "eas.com")
public class SpringBatchPauseStopJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPauseStopJobApplication.class, args);
    }

}
