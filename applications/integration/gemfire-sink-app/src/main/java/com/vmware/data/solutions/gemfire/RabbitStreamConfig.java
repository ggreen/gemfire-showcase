package com.vmware.data.solutions.gemfire;

import com.rabbitmq.stream.OffsetSpecification;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.listener.StreamListenerContainer;

@Configuration
public class RabbitStreamConfig {

    @Value("${rabbitmq.streaming.replay:false}")
    private Boolean replay;

    @Value("${rabbitmq.streaming.replay.offset:0}")
    private Long offset;

    @Value("${rabbitmq.streaming.replay.timestamp:0}")
    private Long timestamp;

    @Value("${spring.application.name:gemfire-sink}")
    private String applicationName = "gemfire-sink";


    /**
     * Override the Java Serializer
     */
    @Bean
    MessageConverter convert()  {
        return new ContentTypeDelegatingMessageConverter();
    }

    @Bean
    ListenerContainerCustomizer<MessageListenerContainer> customizer() {
        //ListenerContainerCustomizer<MessageListenerContainer> listener =
        return  (cont, dest, group) -> {
                var container = (StreamListenerContainer)cont;

                container.setConsumerCustomizer( (name, builder) -> {

                    if (replay) {
                        if (timestamp > 0) {
                            builder.offset(OffsetSpecification.timestamp(timestamp));
                        } else {
                            builder.offset(OffsetSpecification.offset(offset));
                        }
                    } else {
                        builder.offset(
                                        OffsetSpecification.next()
                                ).name(applicationName)
                                .autoTrackingStrategy();
                    }
                });
        };
    }
}
