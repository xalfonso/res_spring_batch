package eas.com;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class BatchConfiguration {

   private List<String> itemToRead = List.of("One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten");

   private List<String> itemWrite = new ArrayList<>();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

   @Bean
   IteratorItemReader<String> itemReader(){
      return new IteratorItemReader<String>(itemToRead);
   }

   @Bean
   ItemProcessor<String, String> itemProcessor(){
       return item -> item + " processed";
   }

   @Bean
   ItemWriter<String> itemWriter(){
       return items -> itemWrite.addAll(items);
   }

    @Bean
    public Step dummyStep(ItemReader<String> reader, ItemProcessor<String, String> processor, ItemWriter<String> writer) {
        return stepBuilderFactory.get("dummyStep")
                .<String, String>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job dummyJob(Step dummyStep) {
        return jobBuilderFactory.get("dummyJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {

                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                          log.info("The job finish, the result list is: {} ", itemWrite);
                    }
                })
                .start(dummyStep)
                .build();
    }


}
