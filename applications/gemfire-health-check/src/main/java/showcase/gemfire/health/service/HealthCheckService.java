package showcase.gemfire.health.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import showcase.gemfire.health.fix.RebalanceCommand;
import java.util.function.Supplier;

/**
 * Execute Check the cluster and execute a autoSafe as needed
 * @author gregory green
 */
@Service
@Slf4j
public class HealthCheckService {

    private final Supplier<Boolean> isRebalanceRequired;

    private final RebalanceCommand rebalanceCommand;

    public HealthCheckService(@Qualifier("IsRebalanceRequired") Supplier<Boolean> isRebalanceRequired,
                              RebalanceCommand rebalanceCommand) {
        this.isRebalanceRequired = isRebalanceRequired;
        this.rebalanceCommand = rebalanceCommand;
    }

    /**
     * If rebalance is required than execute rebalance
     */
    @Scheduled(cron = "${gemfire.check.schedule.cron:0 * * * * *}")
    public void checkAndRepair()  {
        if (isRebalanceRequired.get())
        {
            log.info("Executing rebalance");
            rebalanceCommand.execute();
        }
    }
}
