package dev.nils.notes_modulith.notes.exception;

import dev.nils.notes_modulith.notes.exception.model.NoteErrorCode;
import java.io.Serial;
import lombok.Getter;

@Getter
public class NoteException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3488630058681996995L;

    private final NoteErrorCode errorCode;

    public NoteException(NoteErrorCode errorCode, Object... args) {
        this(errorCode, null, args);
    }

    public NoteException(NoteErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.name() + " - " + String.format(errorCode.getMessage(), args), cause);
        this.errorCode = errorCode;
    }
}
