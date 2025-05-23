package showcase.gemfire.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.gemfire.health.service.HealthCheckService;

@Configuration
@Slf4j
public class StartupConfig {

    @Bean
    CommandLineRunner  commandLineRunner(HealthCheckService service)
    {
        return args -> {
            log.info("Running at startup");
            service.checkAndRepair();
        };
    }
}
