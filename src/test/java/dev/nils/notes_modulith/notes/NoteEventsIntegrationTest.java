package dev.nils.notes_modulith.notes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.TestRabbitListener;
import dev.nils.notes_modulith.notes.domain.NoteService;
import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.notes.web.NoteDto;
import dev.nils.notes_modulith.users.domain.UserRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.modulith.test.Scenario;

@ApplicationModuleTest(extraIncludes = {"common", "users", "notifications"})
@Import({TestContainersConfiguration.class})
class NoteEventsIntegrationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Autowired
    NoteService noteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRabbitListener rabbitListener;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("create() should publish NoteCreatedEvent with expected fields")
    void create_shouldPublishNoteCreatedEvent(AssertablePublishedEvents events) {
        String title = "Event Test Note";

        noteService.create(new NoteCreateRequest(janeId(), title, "Verifying Spring Modulith event publishing."));

        events.assertThat()
                .contains(NoteCreatedEvent.class)
                .matching(NoteCreatedEvent::title, title)
                .matching(e -> e.noteId() != null, true);
    }

    @Test
    @DisplayName("NoteCreatedEvent is handled by the notifications listener")
    void handleNoteCreatedEvent_logsExpectedMessage(Scenario scenario) {
        Logger logger = (Logger) LoggerFactory.getLogger(
                "dev.nils.notes_modulith.notifications.NoteEventNotificationHandler");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        try {
            scenario.publish(new NoteCreatedEvent(999L, "Scenario Test Note"))
                    .andWaitForStateChange(() -> listAppender.list.stream()
                            .anyMatch(e -> e.getFormattedMessage().contains("Note created -> id=999")
                                    && e.getFormattedMessage().contains("Scenario Test Note")))
                    .andVerify(result -> assertThat(result).isTrue());
        } finally {
            logger.detachAppender(listAppender);
        }
    }

    @Test
    @DisplayName("create() externalizes NoteCreatedEvent to RabbitMQ and completes every publication")
    void create_shouldExternalizeEventToRabbitMqAndCompletePublications() {
        String title = "Externalized Note " + UUID.randomUUID();
        NoteDto note = noteService.create(new NoteCreateRequest(janeId(), title, "Sent to RabbitMQ"));

        await().atMost(TIMEOUT).untilAsserted(() -> assertThat(rabbitListener.findByTitle(title))
                .hasValueSatisfying(event -> assertThat(event.noteId()).isEqualTo(note.id())));

        await().atMost(TIMEOUT).untilAsserted(() -> assertThat(publicationsFor(title))
                .as("one publication for the RabbitMQ externalizer and one for the notifications listener")
                .hasSize(2)
                .allSatisfy(row -> assertThat(row.get("completion_date")).isNotNull())
                .anySatisfy(row -> assertThat(row.get("listener_id").toString())
                        .contains("DelegatingEventExternalizer"))
                .anySatisfy(row -> assertThat(row.get("listener_id").toString())
                        .contains("NoteEventNotificationHandler")));
    }

    private Long janeId() {
        return userRepository.findByEmail("jane.doe@example.com").orElseThrow().getId();
    }

    private List<Map<String, Object>> publicationsFor(String title) {
        return jdbcTemplate.queryForList(
                "SELECT listener_id, completion_date FROM events.event_publication"
                        + " WHERE event_type = ? AND serialized_event LIKE ?",
                NoteCreatedEvent.class.getName(),
                "%" + title + "%");
    }
}
