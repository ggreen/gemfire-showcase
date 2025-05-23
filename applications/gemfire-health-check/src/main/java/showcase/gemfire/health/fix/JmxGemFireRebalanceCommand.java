package showcase.gemfire.health.fix;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * JmxGemFireRebalanceCommand using JMX to execute a gfsh rebalance command
 * @author gregory green
 */
@Component
@Slf4j
public class JmxGemFireRebalanceCommand implements RebalanceCommand{

    private final Supplier<Boolean> hasServerCountThreshold;
    private final MemberMXBean locatorBean;
    private final String rebalanceCommand = "rebalance";

    public JmxGemFireRebalanceCommand(@Qualifier("HasServerCountThreshold") Supplier<Boolean> hasServerCountThreshold,
                                      MemberMXBean locatorBean) {
        this.hasServerCountThreshold = hasServerCountThreshold;
        this.locatorBean = locatorBean;
    }

    /**
     * Execute a Gfsh rebalance using the JMX bean
     */
    @Override
    public void execute() {

        var isHasServerCountThreshold = this.hasServerCountThreshold.get();
        log.info("HasServerCountThreshold: {}",isHasServerCountThreshold);

        if(Boolean.TRUE.equals(isHasServerCountThreshold)) {
            log.info("Executing rebalanceCommand: {}",rebalanceCommand);
            var result = locatorBean.processCommand(rebalanceCommand);
            log.info("result: {} from Executed rebalanceCommand: {}",result, rebalanceCommand);
        }
    }
}
