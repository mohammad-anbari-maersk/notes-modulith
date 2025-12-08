package dev.nils.notes_modulith.notes.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NoteErrorCode {
    NOTE_NOT_FOUND_WITH_ID("Note record cannot be found for id value: %s"),
    NOTE_CREATION_FAILED_USER_NOT_FOUND_WITH_ID("Note creation failed, user not found with id value: %s");

    private final String message;
}
