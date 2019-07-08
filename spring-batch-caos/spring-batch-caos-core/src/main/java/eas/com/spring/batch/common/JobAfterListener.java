package eas.com.spring.batch.common;


import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Job After Listener useful for create Job after lister with lambda.
 */
@FunctionalInterface
public interface JobAfterListener extends JobExecutionListener {

    @Override
    default void beforeJob(JobExecution jobExecution) {
        //nothing to do
    }
}
