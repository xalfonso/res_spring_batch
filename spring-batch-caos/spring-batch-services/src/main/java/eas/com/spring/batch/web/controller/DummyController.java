package eas.com.spring.batch.web.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/dummy")
@RestController
public class DummyController {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("importPersonJob")
    @Autowired
    private Job job;


    @GetMapping("/run")
    public ResponseEntity runJob() throws Exception {
        JobExecution run = this.jobLauncher.run(job, new JobParameters());
        return ResponseEntity.ok().build();
    }
}
