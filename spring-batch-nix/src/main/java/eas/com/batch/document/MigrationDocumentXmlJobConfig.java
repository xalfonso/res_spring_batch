package eas.com.batch.document;

import eas.com.batch.SimpleMeasureTime;
import eas.com.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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
    public Tasklet truncateDestinationTable(DataSource dataSource, @Value("#{jobParameters['year']}") String year, @Value("#{jobParameters['truncateDestinationTable']}") String truncateDestinationTable) {
        return (contribution, chunkContext) -> {
            if (Boolean.valueOf(truncateDestinationTable)) {
                new JdbcTemplate(dataSource).execute("TRUNCATE issues" + year);
                log.debug("The table was truncated");
            } else {
                log.debug("The table was not truncate");
            }
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step truncateDestinationTableStep(Tasklet truncateDestinationTable) {
        return stepBuilderFactory.get("truncateDestinationTableStep")
                .tasklet(truncateDestinationTable)
                .build();
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
    public Job migrationIssueJob(Step truncateDestinationTableStep, Step migrationIssueStep) {
        return jobBuilderFactory.get("migrationIssueJob")
                .incrementer(new RunIdIncrementer())
                .start(truncateDestinationTableStep)
                .next(migrationIssueStep)
                .build();
    }


}
