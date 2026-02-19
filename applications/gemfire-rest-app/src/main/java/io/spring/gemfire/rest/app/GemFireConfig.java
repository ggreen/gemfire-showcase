package io.spring.gemfire.rest.app;

import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import io.spring.gemfire.rest.app.service.PdxService;
import org.apache.geode.json.StorageFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GemFireConfig {

    @Value("${spring.data.gemfire.pool.default.locators:localhost[10001]}")
    private String locators;

    @Value("${spring.application:gemfire-rest-app}")
    private String clientName;

    @Bean
    GemFireClient gemFireClient()
    {
        return GemFireClient
                .builder()
                .locators(locators)
                .clientName(clientName).build();
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
