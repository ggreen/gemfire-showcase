package gemfire.showcase.account.web.batch;

import gemfire.showcase.account.web.batch.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.item.*;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
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
@EnableBatchProcessing(tablePrefix = "${batch.job.repository.schema.prefix:}BOOT3_BATCH_")
@Slf4j
public class BatchAppConf {

    private String jobName = "example";


    private String readSql = """
            select * 
            from taccounts.accounts 
            where acct_group = ?
            """;

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
    public JobLauncher jobLauncher(JobRepository jobRepository,
                                   TaskExecutor taskExecutor) throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }


    @StepScope
    @Bean
    public RowMapper<Account> rowMapper()
    {
        return (rs,i) ->   Account.builder()
                .id(rs.getString(1))
                .name(rs.getString(2))
                .groupId(rs.getLong(3))
                .build();
    }

    @Bean
    @StepScope
    ItemStreamReader<Account> reader(RowMapper<Account> rowMapper,
                                     @Value("#{jobParameters['groupId']}") long groupId)
    {
        var dataSource = DataSourceBuilder.create().
        url(batchJdbcUrl).username(batchUsername)
                .password(batchPassword).build();

        var reader = new JdbcCursorItemReaderBuilder<Account>()
                .dataSource(dataSource)
                .name("accounts")
                .rowMapper(rowMapper)
                .sql(readSql)
                .preparedStatementSetter(ps -> {
                    ps.setLong(1,groupId);
                } )
                .fetchSize(fetchSize)
                .build();

        var synchronizeReader = new SynchronizedItemStreamReader<Account>();
        synchronizeReader.setDelegate(reader);
        return synchronizeReader;
    }

    @Bean
    @StepScope
    ItemWriter<Account> writer(GemfireTemplate gemFireTemplate){

        ItemWriter<Account> itemWriter = c ->
            gemFireTemplate.putAll(convertToMap(c));

        return itemWriter;
    }

    @Bean
    @StepScope
    ItemProcessor<Account,Account> itemProcessor()
    {
        //Set current time
        return account -> {
            account.setTimestamp(System.currentTimeMillis());
            log.info("Transformed account: {}",account);
            return account;
        };
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
                   @Qualifier("deleteFromGemFire")
                   Step deleteFromGemFireStep,
                   @Qualifier("loadGemFire")
                   Step loadGemFire){

        return new JobBuilder(jobName,jobRepository)
                .start(deleteFromGemFireStep)
                .next(loadGemFire)
                .build();
    }

    @Bean("loadGemFire")
    public Step loadGemFire(JobRepository jobRepository,
                           @Qualifier("transactionManager")
                           PlatformTransactionManager transactionManager,
                           ItemReader<Account> itemReader,
                           ItemProcessor<Account,Account> processor,
                           ItemWriter<Account> itemWriter) {
        return new StepBuilder("load-step", jobRepository)
                .<Account, Account>chunk(chunkSize, transactionManager)
                .reader(itemReader)
                .processor(processor)
                .writer(itemWriter)
                .build();
    }


    @Bean("deleteFromGemFire")
    public Step deleteAccountsByGroupIdStep(JobRepository jobRepository,
                            @Qualifier("transactionManager")
                            PlatformTransactionManager transactionManager,
                                            GemfireTemplate gemfireTemplate,
                                            Runnable runnable) {

        return new StepBuilder("delete-step", jobRepository)
                .tasklet(toTask(runnable),transactionManager)
                .build();
    }

    @Bean
    @StepScope
    Runnable runnable(@Value("#{jobParameters['groupId']}") int groupId, GemfireTemplate gemfireTemplate)
    {
        return  () -> {
            String[] args = {"select key from /Account.entries where value.groupId = "+groupId};
            log.info("Execute deletes for groupId results {}",groupId);
            var results = FunctionService.onRegion(gemfireTemplate.getRegion()).setArguments(args).execute("DeleteFunction");
            log.info("Delete results {}",results.getResult());
        };
    }

    public MethodInvokingTaskletAdapter toTask(Runnable runnable) {
        MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();

        adapter.setTargetObject(runnable);
        adapter.setTargetMethod("run");
        return adapter;
    }


}
