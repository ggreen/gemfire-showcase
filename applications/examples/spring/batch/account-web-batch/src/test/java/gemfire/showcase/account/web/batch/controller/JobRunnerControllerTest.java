package gemfire.showcase.account.web.batch.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobRunnerControllerTest {
    private JobRunnerController subject;

    @Mock
    private JobLauncher jobLauncher;


    @Mock
    private Job job;

    @Mock
    private JobExecution jobExecution;
    private int groupId = 1;

    @BeforeEach
    void setUp() {
        subject = new JobRunnerController(jobLauncher,job);
    }

    @Test
    void launchJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        long expected = 22L;
        when(jobLauncher.run(any(),any())).thenReturn(jobExecution);
        when(jobExecution.getJobId()).thenReturn(expected);

        var actual = subject.launchForAccountByGroupId(groupId);

        assertThat(actual).isNotNull();

    }
}