package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import io.spring.gemfire.rest.app.service.PdxService;
import org.apache.geode.json.StorageFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GemFireConfig {

    @Bean
    GemFireClient gemFireClient()
    {
        return GemFireClient.connect();
    }

    @Bean
    PdxService pdxService(GemFireClient client)
    {
        return new PdxService(client.getClientCache().getJsonDocumentFactory(StorageFormat.PDX));
    }

    @Bean
    QuerierService querierService(GemFireClient gemfire)
    {
        return gemfire.getQuerierService();
    }
}
