package dev.nils.notes_modulith.notes.web;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Partial update: a {@code null} field keeps the stored value; a provided title must not be blank. */
public record NoteUpdateRequest(
        @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 1000) String title,
        @Size(max = 3000) String content) {}
