package eas.com.spring.batch.job.dummy;

import eas.com.spring.batch.common.JobAfterListener;
import eas.com.spring.batch.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Configuration of Dummy Job.
 *
 * @author Eduardo Alfonso Sanchez
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class DummyJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * Bean for read the person from csv.
     *
     * @return The csv reader person
     */
    @Bean
    public FlatFileItemReader<Person> readerPersonFromCsv() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("readerPersonFromCsv")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "secondName", "age"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    /**
     * Bean for process the person.
     *
     * @return The person processor
     */
    @Bean
    public ItemProcessor<Person, Person> simpleUpperCasePersonProcessor() {
        return person -> new Person(person.getFirstName().toUpperCase(),
                person.getSecondName().toUpperCase(),
                person.getAge());
    }


    @Bean
    public JdbcBatchItemWriter<Person> writerPersonDb(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO person(first_name, second_name, age) VALUES(:firstName, :secondName, :age)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JobAfterListener jobAfterListener(JdbcTemplate jdbcTemplate) {
        return jobExecution -> {
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("The job importPersonJob has finished");

                jdbcTemplate
                        .query("SELECT first_name, second_name, age FROM person",
                                (rs, rowNum) -> new Person(rs.getString(1), rs.getString(2), rs.getInt(3)))
                        .forEach(person -> log.info("Record < " + person + " > "));
            }
        };
    }

    @Bean
    public Step uniqueStep(JdbcBatchItemWriter<Person> writerPersonDb) {
        return stepBuilderFactory.get("uniqueStep")
                .<Person, Person>chunk(10)
                .reader(readerPersonFromCsv())
                .processor(simpleUpperCasePersonProcessor())
                .writer(writerPersonDb)
                .build();
    }

    @Bean
    public Job importPersonJob(JobAfterListener jobAfterListener, Step uniqueStep) {
        return jobBuilderFactory.get("importPersonJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobAfterListener)
                .flow(uniqueStep)
                .end()
                .build();
    }


}
