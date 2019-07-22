package eas.com.batch.document;

import eas.com.batch.SimpleMeasureTime;
import eas.com.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Migration Document Xml Job Config.
 *
 * @author Eduardo Alfonso
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class MigrationDocumentXmlJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private SimpleMeasureTime simpleMeasureTime = new SimpleMeasureTime();


    @Bean
    public JdbcCursorItemReader<Issue> reader(DataSource dataSource) {
        JdbcCursorItemReader<Issue> cursorItemReader = new JdbcCursorItemReader<>();
        cursorItemReader.setDataSource(dataSource);
        cursorItemReader.setSql("SELECT id, kee, rule_id, rule_id, severity FROM issues LIMIT 50000");
        cursorItemReader.setRowMapper((rs, rowNum) -> new Issue()
                .setId(rs.getInt("id"))
                .setKee(rs.getString("kee"))
                .setRuleId(rs.getInt("rule_id"))
                .setSeverity(rs.getString("severity")));
        return cursorItemReader;
    }

    /**
     * Dummy Issue Processor.
     *
     * @return The issue processor
     */
    @Bean
    public ItemProcessor<Issue, Issue> logIssuePersonProcessor() {
        return item -> {
            log.info(Thread.currentThread().getName() + ": Processing issue: " + item);
            return item;
        };
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<Issue> writerIssueDb(DataSource dataSource, @Value("#{jobParameters['year']}") String year) {
        return new JdbcBatchItemWriterBuilder<Issue>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO issues" + year + "(id, kee, rule_id, severity) VALUES(:id, :kee, :ruleId, :severity)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step migrationIssueStep(JdbcCursorItemReader<Issue> reader, ItemProcessor<Issue, Issue> logIssuePersonProcessor, JdbcBatchItemWriter<Issue> writerIssueDb) {

        return stepBuilderFactory.get("migrationIssueStep")
                .<Issue, Issue>chunk(2000)
                .reader(reader)
                .processor(logIssuePersonProcessor())
                .writer(writerIssueDb)
                .build();
    }


    @Bean
    public Job migrationIssueJob(Step migrationIssueStep) {
        return jobBuilderFactory.get("migrationIssueJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("The job with name: {} and id: {} parameters {} has begun at {}",
                                jobExecution.getJobConfigurationName(), jobExecution.getId(), jobExecution.getJobParameters(),
                                jobExecution.getStartTime());
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("[SUCCEFULL COMPLETED JOB] The job with name: {} and id: {} parameters: {} has finished at: {} and duration time: {}",
                                    jobExecution.getJobConfigurationName(), jobExecution.getId(), jobExecution.getJobParameters(),
                                    jobExecution.getEndTime(), jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime());
                        } else {
                            log.info("[NOT SUCCEFULL COMPLETED JOB] The job with name: {} and id: {} parameters: {} has finished at: {} with status: {} and duration time: {} and exceptions: ",
                                    jobExecution.getJobConfigurationName(), jobExecution.getId(), jobExecution.getJobParameters(),
                                    jobExecution.getEndTime(), jobExecution.getStatus(),
                                    jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime(), jobExecution.getAllFailureExceptions());
                        }
                    }
                })
                .flow(migrationIssueStep)
                .end()
                .build();
    }


}
