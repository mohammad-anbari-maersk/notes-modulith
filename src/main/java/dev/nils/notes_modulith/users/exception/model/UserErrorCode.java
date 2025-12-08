package dev.nils.notes_modulith.users.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode {
    USER_NOT_FOUND_WITH_ID("User record cannot be found for id value: %s");

    private final String message;
}
