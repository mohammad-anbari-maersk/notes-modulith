package dev.nils.notes_modulith.notes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(rabbitMQProperties.getExchange());
    }

    @Bean
    Queue newNotesQueue() {
        return new Queue(rabbitMQProperties.getQueue(), false);
    }

    @Bean
    Binding newNotesQueueBinding(Queue newNotesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(newNotesQueue).to(exchange).with(rabbitMQProperties.getRoutingKey());
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JsonMapper jsonMapper) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter(jsonMapper));
        return rabbitTemplate;
    }

    @Bean
    JacksonJsonMessageConverter producerJackson2MessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }
}
