package dev.nils.notes_modulith.notes.domain.model;

import org.springframework.modulith.NamedInterface;
import org.springframework.modulith.events.Externalized;

/** Published when a note is created; the only type of this package that other modules may use. */
@NamedInterface("note-events")
@Externalized
public record NoteCreatedEvent(Long noteId, String title) {}
