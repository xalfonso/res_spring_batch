package eas.com.spring.batch.web.config;

import eas.com.spring.batch.config.BatchConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot Starter Application.
 *
 * @author Eduardo Alfonso Sanchez
 * @since 1.0.0
 */
@Import({BatchConfiguration.class})
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
