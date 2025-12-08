package dev.nils.notes_modulith.notes.config;

import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;

@Configuration
public class NotesEventRoutingConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    public EventExternalizationConfiguration externalizationConfig() {
        return EventExternalizationConfiguration.externalizing()
                .select(EventExternalizationConfiguration.annotatedAsExternalized())
                .route(
                        NoteCreatedEvent.class,
                        evt -> RoutingTarget.forTarget(rabbitMQProperties.getExchange())
                                .andKey(rabbitMQProperties.getRoutingKey())
                )
                .build();
    }
}
