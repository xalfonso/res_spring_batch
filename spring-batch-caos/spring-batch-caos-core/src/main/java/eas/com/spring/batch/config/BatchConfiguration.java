package eas.com.spring.batch.config;

import eas.com.spring.batch.job.dummy.DummyJobConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(DummyJobConfig.class)
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {
}
