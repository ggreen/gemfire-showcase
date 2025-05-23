package showcase.gemfire.health.fix;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(JmxGemFireRebalanceCommand.class);
    private final Supplier<Boolean> hasMemberCountThreshold;
    private final MemberMXBean locatorBean;
    private final String rebalanceCommand = "rebalance";

    public JmxGemFireRebalanceCommand(@Qualifier("HasMemberCountThreshold") Supplier<Boolean> hasMemberCountThreshold,
                                      MemberMXBean locatorBean) {
        this.hasMemberCountThreshold = hasMemberCountThreshold;
        this.locatorBean = locatorBean;
    }

    @Override
    public void execute() {

        var isHasMemberCountThreshold = this.hasMemberCountThreshold.get();
        log.info("isHasMemberCountThreshold: {}",isHasMemberCountThreshold);

        if(Boolean.TRUE.equals(isHasMemberCountThreshold)) {
            log.info("Executing rebalanceCommand: {}",rebalanceCommand);
            var result = locatorBean.processCommand(rebalanceCommand);
            log.info("result: {} from Executed rebalanceCommand: {}",result, rebalanceCommand);
        }
    }
}
