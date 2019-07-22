package eas.com.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MigrationDocumentXmlService {


    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("migrationIssueJob")
    @Autowired
    private Job job;

    @Async
    public void run(String year) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("year", year);
        jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }
}
