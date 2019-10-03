package eas.com;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication(scanBasePackages = "eas.com")
public class SpringBatchAopErrorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchAopErrorsApplication.class, args);
    }

}
