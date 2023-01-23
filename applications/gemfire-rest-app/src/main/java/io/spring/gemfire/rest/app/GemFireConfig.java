package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GemFireConfig {

    @Bean
    GemFireClient geodeClient()
    {
        return GemFireClient.connect();
    }

    @Bean
    QuerierService querierService(GemFireClient gemfire)
    {
        return gemfire.getQuerierService();
    }
}
