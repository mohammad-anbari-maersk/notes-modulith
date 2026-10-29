package dev.nils.notes_modulith.notes.config;

import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;

@Configuration(proxyBeanMethods = false)
public class NotesEventRoutingConfig {

    /**
     * Modulith requests this bean from an {@code EventListenerFactory}, which Spring creates during bean factory
     * post-processing - before autowiring and {@code @ConfigurationProperties} binding are active. The routing
     * properties are therefore bound directly from the {@link Environment}.
     */
    @Bean
    static EventExternalizationConfiguration externalizationConfig(Environment environment) {
        RabbitMQProperties properties = Binder.get(environment)
                .bind("app.rabbitmq", RabbitMQProperties.class)
                .orElseThrow(() -> new IllegalStateException("app.rabbitmq.* properties are required"));
        RoutingTarget noteCreatedTarget =
                RoutingTarget.forTarget(properties.getExchange()).andKey(properties.getRoutingKey());

        return EventExternalizationConfiguration.externalizing()
                .select(EventExternalizationConfiguration.annotatedAsExternalized())
                .route(NoteCreatedEvent.class, event -> noteCreatedTarget)
                .build();
    }
}
