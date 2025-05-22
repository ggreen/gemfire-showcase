package showcase.gemfire.health.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import showcase.gemfire.health.fix.RebalanceCommand;
import showcase.gemfire.health.check.IsMemberMemoryOverThreshold;

/**
 * Execute Check the cluster and execute a autoSafe as needed
 * @author gregory green
 */
@Service
@Slf4j
public class HealthCheckService {

    private final IsMemberMemoryOverThreshold isMemberMemoryOverThreshold;

    private final RebalanceCommand rebalanceCommand;

    public HealthCheckService(IsMemberMemoryOverThreshold isMemberMemoryOverThreshold, RebalanceCommand rebalanceCommand) {
        this.isMemberMemoryOverThreshold = isMemberMemoryOverThreshold;
        this.rebalanceCommand = rebalanceCommand;
    }


    @Scheduled(cron = "0 * * * * *") // every minute at 0 seconds
    public void checkAndRepair()  {
        if (isMemberMemoryOverThreshold.get())
        {
            log.info("Executing rebalance");
            rebalanceCommand.execute();
        }
    }

}
