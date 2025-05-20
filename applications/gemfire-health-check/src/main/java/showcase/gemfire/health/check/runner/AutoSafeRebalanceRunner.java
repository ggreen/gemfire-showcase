package showcase.gemfire.health.check.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.geode.management.MemberMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import showcase.gemfire.health.check.rebalance.RebalanceCommand;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.Set;
import java.util.function.Function;

/**
 * Execute Check the cluster and execute a autoSafe as needed
 * @author gregory green
 */
@Component
@Slf4j
public class AutoSafeRebalanceRunner implements CommandLineRunner {

    private final MBeanServerConnection jmxConnection;
    private final double memoryThreshold;
    private final int rebalanceMinServerCount;
    private final RebalanceCommand rebalanceCommand;
    private final AutoSafeGemFireSupport autoSafeGemFireSupport;

    private final Function<ObjectName,MemberMXBean> getMemberBeanFunction;

    //javax.management.JMX.newMBeanProxy(jmxConnection, member, MemberMXBean.class);

    public AutoSafeRebalanceRunner(MBeanServerConnection jmxConnection,
                                   RebalanceCommand rebalanceCommand,
                                   @Value("${gemfire.threshold.memory.percentage}") double memoryThreshold,
                                   @Value("${gemfire.rebalance.minimum.server.count:0}") int rebalanceMinServerCount,
                                   Function<ObjectName, MemberMXBean> getMemberBeanFunction,
                                   AutoSafeGemFireSupport autoSafeGemFireSupport) {

        this.jmxConnection = jmxConnection;
        this.rebalanceCommand = rebalanceCommand;
        this.memoryThreshold = memoryThreshold;
        this.rebalanceMinServerCount  = rebalanceMinServerCount;
        this.getMemberBeanFunction = getMemberBeanFunction;
        this.autoSafeGemFireSupport = autoSafeGemFireSupport;
    }

    /**
     *
     * @param args the input arguments
     * @throws Exception an general exception occurs
     */
    @Override
    public void run(String... args) throws Exception {
//        String host = "localhost"; // change as needed
//        int port = 1099; // default JMX port unless configured differently

            //JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
//            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            Set<ObjectName> members = jmxConnection.queryNames(new ObjectName("GemFire:type=Member,*"), null);

            MemberMXBean memberBean;
            for (ObjectName member : members) {
                 memberBean =  getMemberBeanFunction.apply(member);

                String name = memberBean.getName();
                Long maxHeap = memberBean.getMaxMemory();
                Long usedHeap = memberBean.getUsedMemory();

                double usagePercentage = ((double) usedHeap / maxHeap) * 100;
                int serverCount;

                log.info("Member: {}, Heap Usage: {}} ({}}dMB/{}}MB)", name, usagePercentage, usedHeap, maxHeap);

                if (usagePercentage > memoryThreshold) {
                    log.info("  WARNING: Member {}} is above heap usage threshold!", name);

                    serverCount = autoSafeGemFireSupport.getServerCount();
                    log.info("Check if rebalanceMinServerCount: {} > serverCount:{}",rebalanceMinServerCount,serverCount);
                    if(serverCount > rebalanceMinServerCount)
                    {
                        log.info("Executing rebalance");
                        rebalanceCommand.execute();
                        return; // do not continue
                    }

                }
            }

    }
}
