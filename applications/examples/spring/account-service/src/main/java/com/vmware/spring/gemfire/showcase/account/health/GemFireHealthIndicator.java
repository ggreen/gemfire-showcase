package com.vmware.spring.gemfire.showcase.account.health;

import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.util.Text;
import org.apache.geode.cache.client.NoAvailableServersException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * GemFire health check
 * @author gregory green
 */
@Component
@Slf4j
public class GemFireHealthIndicator implements HealthIndicator {

    private final GemfireTemplate gemfireTemplate;
    private String key = "1";

    public GemFireHealthIndicator(@Qualifier("HealthTemplate")
                                  GemfireTemplate gemfireTemplate) {
        this.gemfireTemplate = gemfireTemplate;
    }

    @Override
    public Health health() {

        try
        {
            gemfireTemplate.put(key, Text.formatDate(new Date()));
            return Health.up().build();
        }
        catch(RuntimeException e)
        {
            log.warn("ERROR: {}",e);

            if(e instanceof NoAvailableServersException)
                return Health.down().build();

            var cause = e.getCause();

            if(cause instanceof NoAvailableServersException)
                return Health.down().build();

            throw e;
        }
    }
}
