package dev.nils.notes_modulith.notes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {

    private String exchange;

    private String routingKey;

    private String queue;
}
