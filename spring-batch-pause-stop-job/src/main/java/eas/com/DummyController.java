package eas.com;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("dummy")
@RestController
public class DummyController {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("dummyJob")
    @Autowired
    private Job job;

    @GetMapping("service")
    public void run() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date());
        jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }
}
