package dev.nils.notes_modulith.notes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(rabbitMQProperties.getExchange());
    }

    @Bean
    Queue newNotesQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueue()).build();
    }

    @Bean
    Binding newNotesQueueBinding(Queue newNotesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(newNotesQueue).to(exchange).with(rabbitMQProperties.getRoutingKey());
    }

    /** Used by Boot's auto-configured RabbitTemplate and {@code @RabbitListener} containers. */
    @Bean
    JacksonJsonMessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
