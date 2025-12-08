package dev.nils.notes_modulith.notes;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import dev.nils.notes_modulith.TestContainersConfiguration;
import dev.nils.notes_modulith.TestRabbitListener;
import dev.nils.notes_modulith.notes.domain.NoteService;
import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import dev.nils.notes_modulith.notes.web.NoteCreateRequest;
import dev.nils.notes_modulith.users.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.AssertablePublishedEvents;
import org.springframework.modulith.test.Scenario;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(extraIncludes = {"common", "users", "notifications"})
@Import({TestContainersConfiguration.class})
class NoteEventsIntegrationTest {

    @Autowired
    NoteService noteService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    TestRabbitListener listener;

    @BeforeEach
    void clearEventPublications() {
        listener.reset();
    }

    @Test
    @DisplayName("create() should publish NoteCreatedEvent with expected fields")
    void create_shouldPublishNoteCreatedEvent(AssertablePublishedEvents events) {
        Long existingUserId =
                userRepository.findByEmail("jane.doe@example.com").orElseThrow().getId();

        String title = "Event Test Note";
        String content = "Verifying Spring Modulith event publishing.";

        noteService.create(new NoteCreateRequest(existingUserId, title, content));

        events.assertThat()
                .contains(NoteCreatedEvent.class)
                .matching(NoteCreatedEvent::title, title)
                .matching(e -> e.noteId() != null, true);
    }

    @Test
    @DisplayName("NoteCreatedEvent is handled by listener")
    void handleNoteCreatedEvent_logsExpectedMessage(Scenario scenario) {
        // Arrange a test ListAppender on the specific logger used by the handler
        Logger logger =
                (Logger) LoggerFactory.getLogger("dev.nils.notes_modulith.notifications.NoteEventNotificationHandler");
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        var event = new NoteCreatedEvent(999L, "Scenario Test Note");

        // Act: publish and wait until the log appears (use Scenario to avoid sleeps)
        scenario.publish(event)
                .andWaitForStateChange(() -> listAppender.list.stream()
                        .anyMatch(e -> e.getFormattedMessage().contains("Note created -> id=999")
                                && e.getFormattedMessage().contains("Scenario Test Note")))
                .andVerify(result -> assertThat(result).isTrue());

        // Cleanup
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("NoteCreatedEvent is externalized and delivered to RabbitMQ")
    void verifyRabbitMQFlow() throws Exception {
        var event = new NoteCreatedEvent(123L, "Integration Test Note");

        // Act — publish Spring Modulith externalized event
        rabbitTemplate.convertAndSend("notes.exchange", "notes.new", event);

        // Assert — wait for TestRabbitListener to receive message
        boolean received = listener.awaitMessage(10, TimeUnit.SECONDS);
        assertThat(received)
                .as("Message should be received from RabbitMQ queue within 10 seconds")
                .isTrue();

        var receivedEvent = listener.getReceivedEvent();
        assertThat(receivedEvent).as("Received event should not be null").isNotNull();
        assertThat(receivedEvent.noteId()).as("Note ID should match").isEqualTo(123L);
        assertThat(receivedEvent.title()).as("Title should match").isEqualTo("Integration Test Note");
    }

    @Test
    @DisplayName("JDBC Events table should store NoteCreatedEvent publication")
    void verifyJdbcEventsTable(@Autowired JdbcTemplate jdbcTemplate) {

        Long userId =
                userRepository.findByEmail("jane.doe@example.com").orElseThrow().getId();

        // Fire event
        noteService.create(new NoteCreateRequest(userId, "JDBC Event Test", "Check JDBC store"));

        // Query events table
        var rows = jdbcTemplate.queryForList("SELECT * FROM events.event_publication");

        assertThat(rows)
                .as("events.event_publication should contain at least one row")
                .isNotEmpty();

        // Find the NoteCreatedEvent row
        var eventRow = rows.stream()
                .filter(r -> r.get("event_type").toString().contains("NoteCreatedEvent"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No NoteCreatedEvent row found"));

        assertThat(eventRow.get("event_type").toString())
                .contains("NoteCreatedEvent");

        assertThat(eventRow.get("publication_date"))
                .as("publication_date must not be null")
                .isNotNull();

        assertThat(eventRow.get("completion_date"))
                .as("completion_date must be null before listener marks completion")
                .isNull();

        assertThat(eventRow.get("listener_id"))
                .as("listener_id (target bean) must not be empty")
                .isNotNull();
    }

}
