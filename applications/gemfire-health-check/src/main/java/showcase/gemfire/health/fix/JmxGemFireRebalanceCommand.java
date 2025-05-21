package showcase.gemfire.health.fix;

import lombok.RequiredArgsConstructor;
import org.apache.geode.management.MemberMXBean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class JmxGemFireRebalanceCommand implements RebalanceCommand{

    private final Supplier<Boolean> memberCountOverThreshold;
    private final MemberMXBean locatorBean;
    private final String rebalanceCommand = "rebalance";

    @Override
    public void execute() {

        if(Boolean.TRUE.equals(memberCountOverThreshold.get()))
            locatorBean.processCommand(rebalanceCommand);
    }
}
