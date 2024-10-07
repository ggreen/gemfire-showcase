package gemfire.showcase.account.web.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("jobs")
public class JobRunnerController {

    private final JobLauncher jobLauncher;
    private final Job job;


    @SneakyThrows
    @PostMapping("{groupId}")
    public JobInstance launchForAccountByGroupId(@PathVariable int groupId) {

        var jobId = UUID.randomUUID().toString();

        var jobParameterBuilder = new JobParametersBuilder()
                .addJobParameter("jobId",jobId,String.class)
                .addJobParameter("groupId",groupId, Integer.class)
                .addLong("time", System.currentTimeMillis());

        return jobLauncher.run(job,jobParameterBuilder.toJobParameters() ).getJobInstance();
    }
}
