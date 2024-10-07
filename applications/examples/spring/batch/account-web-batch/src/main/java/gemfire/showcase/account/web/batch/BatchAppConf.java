package gemfire.showcase.account.web.batch;

import gemfire.showcase.account.web.batch.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@EnableTask
@EnableTransactionManagement
//${batch.job.repository.schema.prefix:}BOOT3_BATCH_
@EnableBatchProcessing(tablePrefix = "${batch.job.repository.schema.prefix:}BOOT3_BATCH_")
//@EnableAutoConfiguration
@Slf4j
public class BatchAppConf {

    private String jobName = "example";

    @Value("${batch.read.sql}")
    private String readSql;

    @Value("${batch.read.fetch.size}")
    private int fetchSize;

    @Value("${batch.read.chunk.size}")
    private int chunkSize;

    @Value("${batch.jdbc.url}")
    private String batchJdbcUrl;

    @Value("${batch.jdbc.username}")
    private String batchUsername;


    @Value("${batch.jdbc.password:''}")
    private String batchPassword;


    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }


    @Bean
    public RowMapper<Account> rowMapper()
    {
        return (rs,i) ->   Account.builder()
                .id(rs.getString(1))
                .name(rs.getString(2))
                .build();
    }

    @Bean
    ItemReader<Account> reader(RowMapper<Account> rowMapper)
    {
        var dataSource = DataSourceBuilder.create().
        url(batchJdbcUrl).username(batchUsername)
                .password(batchPassword).build();

        return new JdbcCursorItemReaderBuilder<Account>()
                .dataSource(dataSource)
                .name("accounts")
                .rowMapper(rowMapper)
                .sql(readSql)
                .fetchSize(fetchSize)
                .build();
    }

    @Bean
    ItemWriter<Account> writer(GemfireTemplate gemFireTemplate){

        ItemWriter<Account> itemWriter = c ->
            gemFireTemplate.putAll(convertToMap(c));

        return itemWriter;
    }

    protected BinaryOperator<Account> mergeFunction() {
        return (oldValue, newValue) -> {
            log.warn("Duplicate key {}", oldValue);
            return newValue;
        };
    }

    private Map<?,?> convertToMap(Chunk<? extends Account> chunk) {

        Function<Account,String> toKeyFunction = account -> account.getId();

        return  chunk.getItems().parallelStream().collect(
                Collectors.toMap(toKeyFunction, i -> i, mergeFunction()));
    }

    @Bean
    public Job job(JobRepository jobRepository,
//                   @Qualifier("deleteFromGemFire")
//                   Step deleteFromGemFireStep,
                   @Qualifier("loadGemFire")
                   Step loadGemFire){

        return new JobBuilder(jobName,jobRepository)
                .start(loadGemFire)
                .build();
    }

//    @Bean("deleteFromGemFire")
//    public Step loadGemFire(JobRepository jobRepository,
//                            PlatformTransactionManager transactionManager,
//                            GemfireTemplate gemfireTemplate) {
//        Tasklet tasklet;
//        return new StepBuilder("deleteFromGemFire", jobRepository)
//                .tasklet(tasklet,transactionManager).build();
//    }

    @Bean("loadGemFire")
    public Step loadGemFire(JobRepository jobRepository,
                           @Qualifier("transactionManager")
                           PlatformTransactionManager transactionManager,
                           ItemReader<Account> itemReader,
                           ItemWriter<Account> itemWriter) {
        return new StepBuilder("load-step", jobRepository)
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(itemReader)
                .writer(itemWriter)
                .build();
    }

//    @Bean
//    @Order(10)
//    CommandLineRunner runner(Job job,JobLauncher jobLauncher) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        return args -> {
//            String jobId = UUID.randomUUID().toString();
//            JobParameter<?> jobIdParam = new JobParameter<String>(jobId, String.class);
//            JobParameters jobParameters = new JobParameters(Map.of("jobId", jobIdParam));
//            jobLauncher.run(job, jobParameters);
//        };
//    }
}
