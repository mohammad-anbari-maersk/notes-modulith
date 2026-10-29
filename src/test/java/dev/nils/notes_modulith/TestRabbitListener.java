package dev.nils.notes_modulith;

import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Collects every {@link NoteCreatedEvent} delivered to the notes queue so tests can find their own event by its unique title. */
@Component
public class TestRabbitListener {

    private final List<NoteCreatedEvent> receivedEvents = new CopyOnWriteArrayList<>();

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    void receiveMessage(NoteCreatedEvent event) {
        receivedEvents.add(event);
    }

    public Optional<NoteCreatedEvent> findByTitle(String title) {
        return receivedEvents.stream()
                .filter(event -> title.equals(event.title()))
                .findFirst();
    }
}
