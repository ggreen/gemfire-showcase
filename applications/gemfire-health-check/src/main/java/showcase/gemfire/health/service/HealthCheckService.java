package showcase.gemfire.health.service;

import lombok.extern.slf4j.Slf4j;
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


    public void checkAndRepair()  {
//        String host = "localhost"; // change as needed
//        int port = 1099; // default JMX port unless configured differently

        //JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
//            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
//            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        if (isMemberMemoryOverThreshold.get())
        {
            log.info("Executing rebalance");
            rebalanceCommand.execute();
            return; // do not continue
        }
    }

}
