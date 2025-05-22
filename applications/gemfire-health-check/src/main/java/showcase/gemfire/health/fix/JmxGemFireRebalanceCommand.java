package showcase.gemfire.health.fix;

import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class JmxGemFireRebalanceCommand implements RebalanceCommand{

    private final Supplier<Boolean> memberCountOverThreshold;
    private final MemberMXBean locatorBean;
    private final String rebalanceCommand = "rebalance";

    public JmxGemFireRebalanceCommand(@Qualifier("IsMemberCountOverThreshold") Supplier<Boolean> memberCountOverThreshold,
                                      MemberMXBean locatorBean) {
        this.memberCountOverThreshold = memberCountOverThreshold;
        this.locatorBean = locatorBean;
    }

    @Override
    public void execute() {

        if(Boolean.TRUE.equals(memberCountOverThreshold.get()))
            locatorBean.processCommand(rebalanceCommand);
    }
}
