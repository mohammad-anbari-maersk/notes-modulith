package dev.nils.notes_modulith.notes.domain.model;

import org.springframework.modulith.events.Externalized;

@Externalized
public record NoteCreatedEvent(Long noteId, String title) {}
