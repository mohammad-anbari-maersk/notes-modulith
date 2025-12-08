package dev.nils.notes_modulith.users.exception;

import dev.nils.notes_modulith.users.exception.model.UserErrorCode;
import java.io.Serial;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3488630058681996995L;

    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode, Object... args) {
        this(errorCode, null, args);
    }

    public UserException(UserErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.name() + " - " + String.format(errorCode.getMessage(), args), cause);
        this.errorCode = errorCode;
    }
}
