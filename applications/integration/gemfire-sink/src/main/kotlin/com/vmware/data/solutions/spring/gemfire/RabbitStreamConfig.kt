package com.vmware.data.solutions.spring.gemfire

import com.rabbitmq.stream.ConsumerBuilder
import com.rabbitmq.stream.OffsetSpecification
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.stream.config.ListenerContainerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.rabbit.stream.listener.StreamListenerContainer


/**
 * @author Gregory Green
 */
@Configuration
class RabbitStreamConfig {

    @Value("\${rabbitmq.streaming.replay:false}")
    private val replay: Boolean = false;

    @Value("\${rabbitmq.streaming.replay.offset:0}")
    private val offset: Long = 0;

    @Value("\${rabbitmq.streaming.replay.timestamp:0}")
    private val timestamp: Long = 0;

    @Value("\${spring.application.name:apache-geode-sink}")
    private val applicationName: String? = "apache-geode-sink"


    @Bean
    fun customizer(): ListenerContainerCustomizer<MessageListenerContainer> {
        return ListenerContainerCustomizer<MessageListenerContainer> { cont, dest, group ->
            val container = cont as StreamListenerContainer
            container.setConsumerCustomizer { name: String?, builder: ConsumerBuilder ->

                if(replay)
                {
                    if(timestamp > 0)
                    {
                        builder.offset(OffsetSpecification.timestamp(timestamp))
                    }
                    else
                    {
                        builder.offset(OffsetSpecification.offset(offset))
                    }
                }
                else
                {
                    builder.offset(
                        OffsetSpecification.first()
                    ).name(applicationName)
                        .autoTrackingStrategy();
                }
            }
        }
    }
}