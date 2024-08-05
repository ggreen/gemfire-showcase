package showcase.event.stream.rabbitmq.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.tanzu.data.services.rabbitmq.streaming.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import nyla.solutions.core.patterns.conversion.Converter;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class RabbitConsumerConfig {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    ConnectionNameStrategy connectionNameStrategy() {
        return (connectionFactory) -> applicationName;
    }

    @Bean
    Converter<byte[], Account> messageConverter(ObjectMapper objectMapper) {
        return bytes -> {
            try {
                return objectMapper.readValue(bytes,Account.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

}
