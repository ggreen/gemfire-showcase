package showcase.spring.gemfire.hystrix;

import nyla.solutions.core.util.Text;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import showcase.spring.gemfire.hystrix.commands.SupplierHystrixCommand;
import java.util.Calendar;

@Configuration
public class HystrixConfig {

    @Value("${app.batch.size:100}")
    private int batchSize;

    @Value("${app.loopCount:100}")
    private long loopCount;

    @Value("${app.delay.ms}")
    private long delayMs;

    @Value("${hystrix.timeout.ms}")
    private int timeMillisecond;

    @Value("${hystrix.core.size}")
    private int coreSize;

    @Bean
    CommandLineRunner runner( Region<String,String> region)
    {
        return args -> {

            var interaction = 0;
            do {
                for (long i = 0; i < batchSize; i++) {
                    long index  = i;
                    var command = new SupplierHystrixCommand<String>(
                            () -> {
                                var key = Calendar.getInstance().getTime()+"-"+index;
                                region.put(key,key);
                                return region.get(key);
                            }, //function
                            () -> "FALLBACK "+ Calendar.getInstance().getTime(), //fallback
                            timeMillisecond, coreSize);
                    var results = command.execute();

                    System.out.println("results:" + results);
                }
                Thread.sleep(delayMs);
                interaction++;
            } while (interaction < loopCount);

        };
    }

}
