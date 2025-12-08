package dev.nils.notes_modulith.notes.web;

import java.time.Instant;

public record NoteDto(Long id, Instant createdAt, Instant updatedAt, String title, String content, Long userId) {}
