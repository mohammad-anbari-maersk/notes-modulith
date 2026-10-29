package dev.nils.notes_modulith.notes.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NoteCreateRequest(
        @NotNull @Positive Long userId,
        @NotBlank @Size(max = 1000) String title,
        @Size(max = 3000) String content) {}
