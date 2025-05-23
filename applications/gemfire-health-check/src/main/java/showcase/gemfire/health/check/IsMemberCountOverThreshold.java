package showcase.gemfire.health.check;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.DistributedSystemMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;


@Component("IsMemberCountOverThreshold")
@Slf4j
public class IsMemberCountOverThreshold implements Supplier<Boolean> {

    private final DistributedSystemMXBean distributedSystemMBean;
    private final int minimumCacheServerCount;

    public IsMemberCountOverThreshold(DistributedSystemMXBean distributedSystemMBean,
                                      @Value("${gemfire.rebalance.threshold.members.count}")
                                      int minimumCacheServerCount) {
        this.distributedSystemMBean = distributedSystemMBean;
        this.minimumCacheServerCount = minimumCacheServerCount;
    }


    @Override
    public Boolean get() {

        var memberCount = distributedSystemMBean.getMemberCount();
        var locatorCount = distributedSystemMBean.getLocatorCount();

        var cacheServerCount = memberCount - locatorCount;

        log.info("Checking is cacheServerCount: {} >= minimumCacheServerCount:{}",cacheServerCount,minimumCacheServerCount);

        return cacheServerCount >= minimumCacheServerCount;
    }
}
