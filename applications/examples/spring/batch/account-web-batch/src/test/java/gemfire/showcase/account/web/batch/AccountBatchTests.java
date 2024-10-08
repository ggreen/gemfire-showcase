package gemfire.showcase.account.web.batch;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import gemfire.showcase.account.web.batch.domain.Account;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AccountBatchTests {
	private static GemFireCluster gemFireCluster;
	private static int locatorCount = 1;
	private static int serverCount = 1;
	private static String hostName = "localhost";
	private static PostgreSQLContainer<?> postgres;
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	JobRepository jobRepository;

	private Account account;
	private String url = "/jobs/1";

	@BeforeEach
	void setUp() {
		account = JavaBeanGeneratorCreator.of(Account.class).create();
	}

	@BeforeAll
	public static void setup()
	{
		gemFireCluster = new GemFireCluster(System.getProperty("imageName","gemfire/gemfire:10.1-jdk17"), locatorCount, serverCount)
				.withGfsh(true, "create region --name=Account --type=PARTITION")
				.withPorts("locator-0",10334)
				.withPorts("server-0",40404)
				.withHostnameForClients("locator-0",hostName)
				.withHostnameForClients("server-0",hostName)
				.withGemFireProperty("locator-0", "jmx-manager-hostname-for-clients",hostName);



		gemFireCluster.acceptLicense().start();
		System.setProperty("spring.data.gemfire.pool.locators", String.format("localhost[%d]", gemFireCluster.getLocatorPort()));

		postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
				.withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
						new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))
				))
				.withUsername("postgres")
				.withPassword(System.getenv("POSTGRES_DB_PASSWORD"));

		postgres.start();
		System.setProperty("spring.datasource.jdbc.url", postgres.getJdbcUrl());

		System.setProperty("batch.jdbc.url", postgres.getJdbcUrl());

	}


//    @Test
	void jobs() throws InterruptedException {
		gemFireCluster.gfsh(true,"list regions");

		//Create
		var jobInstance  = restTemplate.postForObject(url,null, JobInstance.class);  //error
		assertThat(jobInstance).isNotNull();

		var executions = jobRepository.findJobExecutions(jobInstance);
		assertThat(executions).isNotEmpty();

		for (JobExecution jobExecution: executions)
		{
			while(jobExecution.getStatus().isRunning())
				Thread.sleep(50);

			assertThat(jobExecution.getStatus().isUnsuccessful()).isFalse();
		}

	}

	@AfterAll
	static void shutdown()
	{
		try { gemFireCluster.close(); } catch(Exception e){}
		try { postgres.close(); } catch (Exception e){}

	}

}
