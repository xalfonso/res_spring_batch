package eas.com.config;

import eas.com.batch.document.MigrationDocumentXmlJobConfig;
import eas.com.service.MigrationDocumentXmlService;
import eas.com.web.controller.MigrationDocumentXmlController;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Import({
        MigrationDocumentXmlJobConfig.class,
        MigrationDocumentXmlController.class,
        MigrationDocumentXmlService.class
})
@EnableAsync
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchNixApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchNixApplication.class, args);
    }

}
