package gemfire.showcase.account.web.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("jobs")
public class JobRunnerController {

    private final JobLauncher jobLauncher;
    private final Job job;


    @SneakyThrows
    @PostMapping
    public JobInstance launchForAccountByGroupId(@RequestParam("groupId") int groupId) {


        var jobId = UUID.randomUUID().toString();

        var jobParameterBuilder = new JobParametersBuilder()
                .addJobParameter("jobId",jobId,String.class)
                .addJobParameter("groupId",groupId, Integer.class)
                .addLong("time", System.currentTimeMillis());

        return jobLauncher.run(job,jobParameterBuilder.toJobParameters() ).getJobInstance();
    }
}
