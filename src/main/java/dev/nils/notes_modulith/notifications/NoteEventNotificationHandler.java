package dev.nils.notes_modulith.notifications;

import dev.nils.notes_modulith.notes.domain.model.NoteCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoteEventNotificationHandler {

    @ApplicationModuleListener
    void handleEvent(NoteCreatedEvent event) {
        log.info(
                "[NOTE_EVENT_NOTIFICATION_HANDLER][HANDLE_EVENT]: Note created -> id={}, title={}",
                event.noteId(),
                event.title());
    }
}
